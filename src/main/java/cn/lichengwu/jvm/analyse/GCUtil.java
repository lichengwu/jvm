/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.jvm.analyse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import cn.lichengwu.utils.date.DateUtil;
import cn.lichengwu.utils.lang.Closer;
import cn.lichengwu.utils.lang.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GC工具类
 * 
 * @author lichengwu
 * @created 2011-11-18
 * 
 * @version 1.0
 */
final public class GCUtil {

    private static final Logger logger = LoggerFactory.getLogger(GCUtil.class);

    private static final String GC_FORMAT_REGEX = "(\\d+\\.\\d+): \\[(.*) (\\d+\\.\\d+):([\\s\\S]*)\\[Times: user=(.+) sys=(.+), real=(.+) secs\\]";

    private static final String MINOR_GC_FORMAT_REGEX = ": (\\d+)K->(\\d+)K([\\s\\S]*)(\\d+\\.\\d+)([\\s\\S]*)\\] (\\d+)K->(\\d+)K([\\s\\S]*)(\\d+\\.\\d+)";

    private static final String FULL_GC_FORMAT_REGEX = "CMS: (\\d+)K->(\\d+)K([\\s\\S]*), (\\d+\\.\\d+) secs\\] (\\d+)K->(\\d+)K([\\s\\S]*), \\[CMS Perm : (\\d+)K->(\\d+)K([\\s\\S]*)\\], (\\d+\\.\\d+) secs";

    private static final String TOTAL_VM_INFO_FORMAT_REGEX = "  eden space (\\d+)K,([\\s\\S]*)from space (\\d+)K([\\s\\S]*)concurrent-mark-sweep perm gen total (\\d+)K,([\\s\\S]*)\\)(\\d+\\.\\d+): \\[([\\s\\S]*)\\[([\\s\\S]*)\\]([\\s\\S]*)\\((\\d+)K\\)";

    private static final String CMS_GC_FORMAT_REGEX = "^(\\d+\\.\\d+): \\[GC \\[([\\s\\S]*?)\\[Times: user=(.+?) sys=(.+?), real=(.+?) secs\\]([\\s\\S]*?)YG occupancy([\\s\\S]*?)\\[Times: user=(.+?) sys=(.+?), real=(.+?) secs\\]";

    private static final String TIME_IN_MILLS_REGEX = "(\\d+\\.\\d+):";
    
    private static final String AGE_REGEX = "^- age([\\s\\S]*?)(\\d+):([\\s\\S]*?)(\\d+) bytes,([\\s\\S]*?)total$";

    private static Pattern GC_FORMAT;
    
    private static Pattern AGE_FORMAT;

    private static Pattern MINOR_GC_FORMAT;

    private static Pattern FULL_GC_FORMAT;

    private static Pattern TOTAL_VM_INFO_FORMAT;

    private static Pattern CMS_GC_FORMAT;

    private static Pattern TIME_IN_MILLS;

    static {
        GC_FORMAT = Pattern.compile(GC_FORMAT_REGEX);
        MINOR_GC_FORMAT = Pattern.compile(MINOR_GC_FORMAT_REGEX);
        FULL_GC_FORMAT = Pattern.compile(FULL_GC_FORMAT_REGEX);
        TOTAL_VM_INFO_FORMAT = Pattern.compile(TOTAL_VM_INFO_FORMAT_REGEX);
        CMS_GC_FORMAT = Pattern.compile(CMS_GC_FORMAT_REGEX);
        TIME_IN_MILLS = Pattern.compile(TIME_IN_MILLS_REGEX);
        AGE_FORMAT = Pattern.compile(AGE_REGEX);
    }

    private GCUtil() {
    }

    /**
     * 获得应用的GC信息
     * 
     * @author lichengwu
     * @created 2011-11-19
     * 
     * @param appFile
     * @return
     */
    public static Map<String, Object> getAppGCInfoMap(File appFile) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<GCType, List<GCInfo>> gcStackMap = new ConcurrentHashMap<GCType, List<GCInfo>>();
        List<ErrorMessage> errList = new ArrayList<ErrorMessage>();
        BufferedReader br = null;
        Map<String, AgeVo> ageMap = new LinkedHashMap<String, AgeVo>();
        try {
            if (!appFile.exists() || appFile.isDirectory()) {
                return map;
            }
            logger.info("begin analyse log:{}", appFile.getCanonicalPath());
            br = new BufferedReader(new FileReader(appFile));
            String line = null;
            String bootTime = null;
            // 跳过参数行
            try {
                do {
                    line = br.readLine();
                    if (line.startsWith("BootTime")) {
                        bootTime = line.split("=")[1];
                    }
                    if (line.startsWith("GCConfig")) {
                        break;
                    }
                } while (line != null);
            } catch (Throwable ex) {
                logger.error(ex.getMessage());
                return map;
            }
            StringBuilder logInfo = new StringBuilder();
            StringBuilder lastInfo = null;
            StringBuilder forLastTime = null;
            int lineCount = 0;
            String lastRebootTime = null;
            String lastCMSGC = "";
            line = br.readLine();
            do {
                line = trimLine(line);
                Matcher ageMatcher = AGE_FORMAT.matcher(line);
                if(ageMatcher.find()){
                    String age = ageMatcher.group(2);
                    Long size = Long.valueOf(ageMatcher.group(4));
                    AgeVo ageVo = ageMap.get(age);
                    if(ageVo!=null){
                        ageVo.setSize(ageVo.getSize()+size);
                        ageVo.addTimes();
                    }else{
                        ageVo = new AgeVo();
                        ageVo.setSize(size);
                        ageVo.setTimes(1L);
                        ageMap.put(age, ageVo);
                    }
                }
                
                try {
                    if (line.contains("concurrent mode failure")) {
                        ErrorMessage errMsg = new ErrorMessage();
                        errMsg.setMsg("concurrent mode failure");
                        Matcher matcher = TIME_IN_MILLS.matcher(logInfo);
                        if (matcher.find()) {
                            NumberFormat nf = NumberFormat.getNumberInstance(Locale.CHINA);
                            if (lastRebootTime != null) {
                                errMsg.setOccurTime(new Date(DateUtil.string2Date(lastRebootTime,
                                        DateUtil.DefaultLongFormat).getTime()
                                        + Long.valueOf(nf.format(
                                                Double.valueOf(matcher.group(1)) * 1000)
                                                .replaceAll(",", ""))));
                            } else {
                                errMsg.setOccurTime(new Date(DateUtil.string2Date(bootTime,
                                        DateUtil.DefaultLongFormat).getTime()
                                        + Long.valueOf(nf.format(
                                                Double.valueOf(matcher.group(1)) * 1000)
                                                .replaceAll(",", ""))));
                            }

                        }
                        errList.add(errMsg);
                    }
                    if (line.contains("promotion failed")) {
                        ErrorMessage errMsg = new ErrorMessage();
                        errMsg.setMsg("promotion failed");
                        Matcher matcher = TIME_IN_MILLS.matcher(line);
                        if (matcher.find()) {
                            NumberFormat nf = NumberFormat.getNumberInstance(Locale.CHINA);
                            if (lastRebootTime != null) {
                                errMsg.setOccurTime(new Date(DateUtil.string2Date(lastRebootTime,
                                        DateUtil.DefaultLongFormat).getTime()
                                        + Long.valueOf(nf.format(
                                                Double.valueOf(matcher.group(1)) * 1000)
                                                .replaceAll(",", ""))));
                            } else {
                                errMsg.setOccurTime(new Date(DateUtil.string2Date(bootTime,
                                        DateUtil.DefaultLongFormat).getTime()
                                        + Long.valueOf(nf.format(
                                                Double.valueOf(matcher.group(1)) * 1000)
                                                .replaceAll(",", ""))));
                            }
                        }
                        errList.add(errMsg);
                    }
                    lineCount++;
                    if (StringUtil.isBlank(line) || line.startsWith("BootTime")
                            || line.startsWith("GCConfig")) {
                        if (line.startsWith("BootTime")) {
                            lastRebootTime = line.split("=")[1];
                        }
                        lineCount = 0;
                        logInfo = new StringBuilder();
                        line = br.readLine();
                        continue;
                    }
                    // full GC
                    if (line.startsWith("{") && logInfo.length() > 1 && logInfo.charAt(0) != '{'
                            && logInfo.charAt(logInfo.length() - 1) != '}') {
                        GCInfo cmsgcInfo = getCMSGCInfo(logInfo.toString());
                        if (cmsgcInfo != null) {
                            List<GCInfo> list = gcStackMap.get(cmsgcInfo.getType());
                            if (list != null) {
                                list.add(cmsgcInfo);
                            } else {
                                list = new ArrayList<GCInfo>();
                                list.add(cmsgcInfo);
                                gcStackMap.put(cmsgcInfo.getType(), list);
                            }
                            lastCMSGC = "";
                        } else {
                            // 处理full gc cms 碎片
                            if (logInfo.indexOf("CMS-initial-mark") > 1) {
                                lastCMSGC = logInfo.toString();
                            } else {
                                lastCMSGC += logInfo.toString();
                            }

                            GCInfo patchCmsgcInfo = getCMSGCInfo(lastCMSGC);
                            if (patchCmsgcInfo != null) {
                                List<GCInfo> list = gcStackMap.get(patchCmsgcInfo.getType());
                                if (list != null) {
                                    list.add(patchCmsgcInfo);
                                } else {
                                    list = new ArrayList<GCInfo>();
                                    list.add(patchCmsgcInfo);
                                    gcStackMap.put(patchCmsgcInfo.getType(), list);
                                }
                                lastCMSGC = "";
                            }
                        }
                        forLastTime = logInfo;
                        logInfo = new StringBuilder();
                        logInfo.append(line);
                        lineCount = 0;
                        line = br.readLine();
                        continue;
                    }
                    if (lineCount == 13 && logInfo.charAt(0) != '{') {
                        GCInfo cmsgcInfo = getCMSGCInfo(logInfo.toString());
                        if (cmsgcInfo != null) {
                            List<GCInfo> list = gcStackMap.get(cmsgcInfo.getType());
                            if (list != null) {
                                list.add(cmsgcInfo);
                            } else {
                                list = new ArrayList<GCInfo>();
                                list.add(cmsgcInfo);
                                gcStackMap.put(cmsgcInfo.getType(), list);
                            }
                        }
                        forLastTime = logInfo;
                        logInfo = new StringBuilder();
                        logInfo.append(line);
                        lineCount = 0;
                        line = br.readLine();
                        continue;
                    }
                    // 普通GC
                    if ("}".equals(line)) {
                        logInfo.append(line);
                        // 不完整内容跳过
                        if (logInfo.charAt(0) != '{') {
                            logInfo = new StringBuilder();
                            lineCount = 0;
                            line = br.readLine();
                            continue;
                        } else if (logInfo.charAt(0) == '{'
                                && logInfo.charAt(logInfo.length() - 1) == '}') {
                            Matcher matcher = GC_FORMAT.matcher(logInfo.toString());
                            if (matcher.find()) {
                                GCInfo info = new GCInfo();
                                info.setOccurTime(Double.valueOf(matcher.group(1)));
                                info.setType(GCType.getByName(matcher.group(2)));
                                info.setUserCPU(Double.valueOf(matcher.group(5)));
                                info.setSysCPU(Double.valueOf(matcher.group(6)));
                                info.setTotalTime(Double.valueOf(matcher.group(7)));
                                fillOtherInfo(matcher.group(4), info);
                                List<GCInfo> list = gcStackMap.get(info.getType());
                                if (list != null) {
                                    list.add(info);
                                } else {
                                    list = new ArrayList<GCInfo>();
                                    list.add(info);
                                    gcStackMap.put(info.getType(), list);
                                }
                            }
                            forLastTime = logInfo;
                            lastInfo = logInfo;
                            logInfo = new StringBuilder();
                            lineCount = 0;
                            line = br.readLine();
                            continue;
                        }
                    }
                    logInfo.append(line);
                    line = br.readLine();
                } catch (Throwable e) {
                    logger.warn("exception occur while parsing GC info, ignored. ({})",
                            e.getMessage());
                    logInfo = new StringBuilder();
                    line = br.readLine();
                    continue;
                }
            } while (line != null);
            JvmInfo totalVmInfo = getTotalVmInfo(lastInfo.toString(), appFile);

            Pattern pattern = Pattern.compile("([\\s\\S]*) (\\d+\\.\\d+):");

            Matcher matcher = pattern.matcher(forLastTime);
            if (matcher.find()) {
                NumberFormat nf = NumberFormat.getNumberInstance(Locale.CHINA);
                String group = matcher.group(2);
                if (lastRebootTime == null) {
                    totalVmInfo.setEndTime(new Date(totalVmInfo.getBeginTime().getTime()
                            + (Long.valueOf(nf.format(Double.valueOf(group) * 1000).replaceAll(",",
                                    ""))) - getGcTimeStart(appFile)));
                } else {
                    totalVmInfo.setEndTime(new Date(DateUtil.string2Date(lastRebootTime,
                            DateUtil.DefaultLongFormat).getTime()
                            + Long.valueOf(nf.format(Double.valueOf(group) * 1000).replaceAll(",",
                                    ""))));
                }
            } else {
                logger.error("can not find gc end time info from last gc info");
            }

            int fullGc = gcStackMap.get(GCType.FULL_GC) == null ? 0 : gcStackMap
                    .get(GCType.FULL_GC).size();
            int fullGcCMS = gcStackMap.get(GCType.FULL_GC_CMS) == null ? 0 : gcStackMap.get(
                    GCType.FULL_GC_CMS).size();
            int fllGcSystem = gcStackMap.get(GCType.FULL_GC_SYSTEM) == null ? 0 : gcStackMap.get(
                    GCType.FULL_GC_SYSTEM).size();
            totalVmInfo.setFullGCTims(fllGcSystem + fullGc + fullGcCMS);
            totalVmInfo.setMinorGCTimes(gcStackMap.get(GCType.MINOR_GC).size());
            totalVmInfo
                    .setTotalGCTimes(totalVmInfo.getFullGCTims() + totalVmInfo.getMinorGCTimes());
            map.put("vmInfo", totalVmInfo);
            map.put("stack", gcStackMap);
            logger.info("finish analyse log:{}", appFile.getCanonicalPath());
        } catch (Exception e) {
            logger.error("error occur while parsing GC info for app:{},({})",
                    appFile.getAbsolutePath(), e.getMessage());
        } finally {
            Closer.close(br);
        }
        if (!map.isEmpty()) {
//            JvmInfo info = (JvmInfo) map.get("vmInfo");
//            for (Iterator<ErrorMessage> it = errList.iterator(); it.hasNext();) {
//                ErrorMessage err = it.next();
//                err.setOccurTime(new Date(info.getBeginTime().getTime()
//                        + err.getOccurTime().getTime()));
//            }
            map.put("errMsg", errList);
            map.put("ageInfo", ageMap);
        }
        return map;
    }

    /**
     * 填充其他信息
     * 
     * @author lichengwu
     * @created 2011-11-19
     * 
     * @param data
     * @param info
     */
    private static void fillOtherInfo(String data, GCInfo info) {
        if (info.getType().equals(GCType.MINOR_GC)) {
            Matcher matcher = MINOR_GC_FORMAT.matcher(data);
            if (matcher.find()) {
                info.setBeforeGCYoungAvailableSize(Long.valueOf(matcher.group(1)));
                info.setAfterGCYoungAvailableSize(Long.valueOf(matcher.group(2)));
                info.setBeforeGCHeapAvailableSize(Long.valueOf(matcher.group(6)));
                info.setAfterGCHeapAvailableSize(Long.valueOf(matcher.group(7)));
                info.setYongTime(Double.valueOf(matcher.group(9)));
            } else {
                logger.warn("GC log:{} did not contain correct format info.", data);
            }
        } else {
            Matcher matcher = FULL_GC_FORMAT.matcher(data);
            if (matcher.find()) {
                info.setBeforeGCYoungAvailableSize(Long.valueOf(matcher.group(1)));
                info.setAfterGCYoungAvailableSize(Long.valueOf(matcher.group(2)));
                info.setYongTime(Double.parseDouble(matcher.group(4)));
                info.setBeforeGCHeapAvailableSize(Long.valueOf(matcher.group(5)));
                info.setAfterGCHeapAvailableSize(Long.valueOf(matcher.group(6)));
                info.setBeforeGCPermAvailableSize(Long.valueOf(matcher.group(8)));
                info.setAfterGCPermAvailableSize(Long.valueOf(matcher.group(9)));
                info.setPermTime(Double.valueOf(matcher.group(11)));
            } else {
                logger.warn("GC log:{} did not contain correct format info.", data);
            }
        }
    }

    /**
     * 从日志文件中获得相对开始时间
     * 
     * @author lichengwu
     * @created 2011-11-24
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public static Long getGcTimeStart(File file) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            Long date = 0L;
            String line = br.readLine();
            Pattern pattern = Pattern.compile("^(\\d+\\.\\d+):([\\s\\S]*)");
            do {
                line = trimLine(line);
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    NumberFormat nf = NumberFormat.getNumberInstance(Locale.CHINA);
                    String group = matcher.group(1);
                    date = Long
                            .valueOf(nf.format(Double.valueOf(group) * 1000).replaceAll(",", ""));
                    break;
                }
                line = br.readLine();
            } while (line != null);
            return date;
        } finally {
            Closer.close(br);
        }

    }

    /**
     * 从一个完整的log中获得jvm信息
     * 
     * @author lichengwu
     * @created 2011-11-19
     * 
     * @param aFullGCInfoStack
     * @param app
     * @return
     * @throws IOException
     */
    private static JvmInfo getTotalVmInfo(String aFullGCInfoStack, File app) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(app));
            String line = br.readLine();
            String bootTime = null;
            String info = null;
            do {
                line = trimLine(line);
                if (line.startsWith("BootTime")) {
                    bootTime = line;
                }
                if (line.startsWith("GCConfig")) {
                    info = line;
                    break;
                }
                line = br.readLine();
            } while (line != null);
            br.close();
            Date startTime = new Date(DateUtil.string2Date(bootTime.split("=")[1],
                    DateUtil.DefaultLongFormat).getTime()
                    + getGcTimeStart(app));

            JvmInfo jvmInfo = new JvmInfo(getAppKey(app));
            jvmInfo.setInfo(info.split("=")[1]);
            jvmInfo.setBeginTime(startTime);
            Matcher matcher = TOTAL_VM_INFO_FORMAT.matcher(aFullGCInfoStack);
            if (matcher.find()) {
                jvmInfo.setEdenSize(Long.valueOf(matcher.group(1)));
                jvmInfo.setFromSize(Long.valueOf(matcher.group(3)));
                jvmInfo.setToSize(Long.valueOf(matcher.group(3)));
                jvmInfo.setPermGenSize(Long.valueOf(matcher.group(5)));
                jvmInfo.setHeapSize(Long.valueOf(matcher.group(11)));
                jvmInfo.setOldGenSize(jvmInfo.getHeapSize() - jvmInfo.getEdenSize()
                        - jvmInfo.getFromSize() * 2);
                jvmInfo.setYongGenSize(jvmInfo.getFromSize() + jvmInfo.getToSize()
                        + jvmInfo.getEdenSize());
            } else {
                throw new RuntimeException("can not find jvm info in CInfoStack:"
                        + aFullGCInfoStack);
            }
            return jvmInfo;
        } finally {
            if (br != null) {
                br.close();
            }
        }

    }

    /**
     * 获得CMS GC info
     * 
     * @author lichengwu
     * @created 2011-11-22
     * 
     * @param data
     * @return
     */
    private static GCInfo getCMSGCInfo(String data) {
        GCInfo info = new GCInfo();
        Matcher matcher = CMS_GC_FORMAT.matcher(data);
        if (matcher.find()) {
            info.setType(GCType.FULL_GC_CMS);
            info.setOccurTime(Double.valueOf(matcher.group(1)));
            info.setUserCPU(Double.valueOf(matcher.group(3)) + Double.valueOf(matcher.group(8)));
            info.setSysCPU(Double.valueOf(matcher.group(4)) + Double.valueOf(matcher.group(9)));
            info.setTotalTime(Double.valueOf(matcher.group(5)) + Double.valueOf(matcher.group(10)));
            return info;
        }
        return null;
    }

    /**
     * 删除前面的时间
     * 
     * @author lichengwu
     * @created 2011-12-8
     * 
     * @param line
     * @return
     */
    private static String trimLine(String line) {
        return line.replaceFirst("^\\d{4}-\\d{2}-\\d{2}([\\s\\S]*?):\\s", "");
    }

    /**
     * 获得日志的app
     * 
     * @author lichengwu
     * @created 2011-11-23
     * 
     * @param logFile
     * @return
     */
    public static String getAppKey(File logFile) {
        if (logFile.exists() && logFile.isFile()) {
            String name = logFile.getName();
            return name.substring(0, name.indexOf('.'));
        }
        return null;
    }

    public static void main(String[] args) {

        // Map<String, Object> map = getAppGCInfoMap("mtct");
        // JvmInfo jvmInfo = (JvmInfo) map.get("vmInfo");
        // System.out.println(jvmInfo.getEndTime().getTime() -
        // jvmInfo.getBeginTime().getTime());
        // System.out.println("周期："
        // + (jvmInfo.getEndTime().getTime() - jvmInfo.getBeginTime().getTime())
        // / (jvmInfo.getTotalGCTimes() * 1000));
        // System.out.println(jvmInfo.getMinorGCTimes());
        // System.out.println(jvmInfo.getFullGCTims());
        // System.out.println(jvmInfo.getTotalGCTimes());
        // long before = 0l;
        // long after = 0l;
        // double total = 996160;
        // long iCount = 0;
        // long yongBefore = 0L;
        // long yongAfter = 0L;
        // double totalTime = 0d;
        // double yongTime = 0d;
        // double fullTime = 0d;
        // for (Entry<GCType, List<GCInfo>> entry : map.entrySet()) {
        // for (GCInfo info : entry.getValue()) {
        // iCount++;
        // before += info.getBeforeGCHeapAvailableSize();
        // after += info.getAfterGCHeapAvailableSize();
        // yongBefore += info.getBeforeGCYoungAvailableSize();
        // yongAfter += info.getAfterGCYoungAvailableSize();
        // totalTime += info.getTotalTime() == 0D ? info.getYongTime() :
        // info.getTotalTime();
        // yongTime += info.getYongTime();
        // if (!info.getType().equals(GCType.MINOR_GC)) {
        // fullTime += info.getTotalTime();
        // }
        // }
        // }
        // System.out.println(before / iCount);
        // System.out.println((before - after) / iCount);
        // System.out.println(yongBefore / iCount);
        // System.out.println((yongBefore - yongAfter) / iCount);
        // System.out.println("垃圾回收耗时:" + totalTime / iCount);
        // System.out.println("新生代耗时:" + yongTime / iCount);
        // System.out.println("full gc avg time:" + fullTime / 15);
        // System.out.println(map.get(GCType.FULL_GC_SYSTEM).size());
        // System.out.println(map.get(GCType.FULL_GC).size());
        // System.out.println(map.get(GCType.MINOR_GC).size());
        // String ss =
        // "{Heap before GC invocations=409 (full 15): par new generation   total 471872K, used 422119K [0x00002aaaae400000, 0x00002aaace400000, 0x00002aaace400000)  eden space 419456K, 100% used [0x00002aaaae400000, 0x00002aaac7da0000, 0x00002aaac7da0000)  from space 52416K,   5% used [0x00002aaac7da0000, 0x00002aaac8039d80, 0x00002aaacb0d0000)  to   space 52416K,   0% used [0x00002aaacb0d0000, 0x00002aaacb0d0000, 0x00002aaace400000) concurrent mark-sweep generation total 524288K, used 90087K [0x00002aaace400000, 0x00002aaaee400000, 0x00002aaaee400000) concurrent-mark-sweep perm gen total 262144K, used 78239K [0x00002aaaee400000, 0x00002aaafe400000, 0x00002aaafe400000)51678.768: [GC 51678.768: [ParNewDesired survivor size 26836992 bytes, new threshold 15 (max 15)- age   1:    1921512 bytes,    1921512 total- age   4:     256552 bytes,    2178064 total- age   6:     512296 bytes,    2690360 total- age   9:     595576 bytes,    3285936 total- age  10:     351696 bytes,    3637632 total: 422119K->4386K(471872K), 0.0028740 secs] 512207K->94474K(996160K), 0.0029630 secs] [Times: user=0.02 sys=0.00, real=0.01 secs] Heap after GC invocations=410 (full 15): par new generation   total 471872K, used 4386K [0x00002aaaae400000, 0x00002aaace400000, 0x00002aaace400000)  eden space 419456K,   0% used [0x00002aaaae400000, 0x00002aaaae400000, 0x00002aaac7da0000)  from space 52416K,   8% used [0x00002aaacb0d0000, 0x00002aaacb518998, 0x00002aaace400000)  to   space 52416K,   0% used [0x00002aaac7da0000, 0x00002aaac7da0000, 0x00002aaacb0d0000) concurrent mark-sweep generation total 524288K, used 90087K [0x00002aaace400000, 0x00002aaaee400000, 0x00002aaaee400000) concurrent-mark-sweep perm gen total 262144K, used 78239K [0x00002aaaee400000, 0x00002aaafe400000, 0x00002aaafe400000)}";
        // Matcher matcher = TOTAL_VM_INFO_FORMAT.matcher(ss);
        // if (matcher.find()) {
        // System.out.println("eden:" + matcher.group(1));
        // System.out.println("from:" + matcher.group(3));
        // System.out.println("to:" + matcher.group(3));
        // System.out.println("perm gen:" + matcher.group(5));
        // System.out.println("last time" + matcher.group(7));
        // System.out.println("heap size:" + matcher.group(11));
        // }

        // Matcher matcher = CMS_GC_FORMAT
        // .matcher("5791.121: [GC [1 CMS-initial-mark: 1837900K(3670016K)] 1883659K(4141888K), 0.0534830 secs] [Times: user=0.05 sys=0.00, real=0.05 secs] 5791.175: [CMS-concurrent-mark-start]5791.486: [CMS-concurrent-mark: 0.311/0.311 secs] [Times: user=1.30 sys=0.05, real=0.31 secs] 5791.486: [CMS-concurrent-preclean-start]5791.503: [CMS-concurrent-preclean: 0.017/0.017 secs] [Times: user=0.02 sys=0.00, real=0.02 secs] 5791.503: [CMS-concurrent-abortable-preclean-start] CMS: abort preclean due to time 5796.565: [CMS-concurrent-abortable-preclean: 2.717/5.062 secs] [Times: user=3.32 sys=0.08, real=5.06 secs] 5796.583: [GC[YG occupancy: 184478 K (471872 K)]5796.583: [Rescan (parallel) , 0.0533500 secs]5796.637: [weak refs processing, 0.0374370 secs] [1 CMS-remark: 1837900K(3670016K)] 2022379K(4141888K), 0.0917240 secs] [Times: user=0.40 sys=0.04, real=0.09 secs] 5796.675: [CMS-concurrent-sweep-start]5800.802: [CMS-concurrent-sweep: 4.127/4.127 secs] [Times: user=4.48 sys=0.05, real=4.13 secs] 5800.802: [CMS-concurrent-reset-start]5800.823: [CMS-concurrent-reset: 0.021/0.021 secs] [Times: user=0.02 sys=0.01, real=0.02 secs] ");
        // if (matcher.find()) {
        // System.out.println(matcher.group(1));
        // System.out.println(matcher.group(3));
        // System.out.println(matcher.group(4));
        // System.out.println(matcher.group(5));
        // System.out.println(matcher.group(8));
        // System.out.println(matcher.group(9));
        // System.out.println(matcher.group(10));
        // }
        // System.out.println(new Date().getTime());
        String str = "- age   1:    5060728 bytes,    5060728 total";
        Matcher matcher = Pattern.compile("^- age([\\s\\S]*?)(\\d+):([\\s\\S]*?)(\\d+) bytes,([\\s\\S]*?)total$").matcher(str);
        if(matcher.find()){
            System.out.println(matcher.group(2)+":"+matcher.group(4));
        }
    }
}
