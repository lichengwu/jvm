/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.jvm.analyse;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


import cn.lichengwu.utils.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * GCManager
 * 
 * @author lichengwu
 * @created 2011-11-21
 * 
 * @version 1.0
 */
public class GCManager {

    private static final Logger logger = LoggerFactory.getLogger(GCManager.class);

//    @Resource
//    SendMailService sendMailService;

    /**
     * 发送报告
     * 
     * @author lichengwu
     * @created 2011-11-22
     * 
     * @return
     */
    public Map<String, Object> report() {
//        File gcLogPath = new File(ConfigUtil.getValueByKey("jvm.gc.log.path"));
    	File gcLogPath = new File("jvm.gc.log.path");
        String date = DateUtil.date2String(DateUtil.toYesterday(new Date()), "yyyy-MM-dd");
        final String suffix = ".gc.log." + date;
        File[] logFiles = gcLogPath.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(suffix);
            }
        });
        Map<String, Object> report = new LinkedHashMap<String, Object>();
        // 排序用的map
        Map<Long, String> orderMap = new TreeMap<Long, String>(new Comparator<Long>() {
            @Override
            public int compare(Long o1, Long o2) {
                return o1.compareTo(o2);
            }
        });
        if (logFiles == null || logFiles.length < 1) {
            logger.warn("no data found,report will not be sent!");
            return report;
        }
        Integer maxAge = 1;
        for (File file : logFiles) {
            Double totalGcTime = 0D;
            Double fullGcTime = 0D;
            Double minorGcTime = 0D;
            Map<String, Object> appData = new LinkedHashMap<String, Object>();
            Map<String, Object> appGCInfoMap = GCUtil.getAppGCInfoMap(file);
            if (appGCInfoMap.isEmpty()) {
                continue;
            }
            JvmInfo jvmInfo = (JvmInfo) appGCInfoMap.get("vmInfo");
            appData.put("jvm_info", jvmInfo);
            @SuppressWarnings("unchecked")
            Map<GCType, List<GCInfo>> gcMap = (Map<GCType, List<GCInfo>>) appGCInfoMap.get("stack");
            long totalRunTime = jvmInfo.getEndTime().getTime() - jvmInfo.getBeginTime().getTime();
            appData.put("avg_gc_period", jvmInfo.getTotalGCTimes() == 0 ? 0 : totalRunTime
                    / (jvmInfo.getTotalGCTimes() * 1000));
            appData.put("avg_full_gc_period", jvmInfo.getFullGCTims() == 0 ? 0 : totalRunTime
                    / (jvmInfo.getFullGCTims() * 1000));
            appData.put("avg_minor_gc_period", jvmInfo.getMinorGCTimes() == 0 ? 0 : totalRunTime
                    / (jvmInfo.getMinorGCTimes() * 1000));
            for (Entry<GCType, List<GCInfo>> entry : gcMap.entrySet()) {
                for (GCInfo gci : entry.getValue()) {
                    totalGcTime += gci.getTotalTime() == 0D ? gci.getYongTime() : gci
                            .getTotalTime();
                    if (gci.getType().equals(GCType.MINOR_GC)) {
                        minorGcTime += gci.getYongTime();
                    } else {
                        fullGcTime += gci.getTotalTime();
                    }
                }
            }
            appData.put("total_gc_time", totalGcTime);
            appData.put("full_gc_time", fullGcTime);
            appData.put("minor_gc_time", minorGcTime);
            appData.put("gc_time_ratio", totalGcTime * 100000D / totalRunTime);
            appData.put("full_gc_time_ratio", fullGcTime * 100000D / totalRunTime);
            appData.put("avg_gc_time", totalGcTime / jvmInfo.getTotalGCTimes());
            appData.put("avg_full_gc_time",
                    jvmInfo.getFullGCTims() == 0 ? 0 : fullGcTime / jvmInfo.getFullGCTims());
            appData.put("avg_minor_gc_time", minorGcTime / jvmInfo.getMinorGCTimes());
            appData.put("err_msg", appGCInfoMap.get("errMsg"));
            @SuppressWarnings("unchecked")
            Map<String, AgeVo> ageMap = (Map<String, AgeVo>) appGCInfoMap.get("ageInfo");
            appData.put("ageInfo", ageMap);
            Long totalAgeSize = 1L;
            maxAge = Math.max(maxAge, ageMap.size());
            for (Entry<String, AgeVo> entry : ageMap.entrySet()) {
                totalAgeSize += entry.getValue().getSize();
            }
            appData.put("totalAgeSize", totalAgeSize);
            List<GCInfo> full_gc_list = new ArrayList<GCInfo>();
            if (gcMap.get(GCType.FULL_GC) != null) {
                full_gc_list.addAll(gcMap.get(GCType.FULL_GC));
            }
            if (gcMap.get(GCType.FULL_GC_SYSTEM) != null) {
                full_gc_list.addAll(gcMap.get(GCType.FULL_GC_SYSTEM));
            }
            if (gcMap.get(GCType.FULL_GC_CMS) != null) {
                full_gc_list.addAll(gcMap.get(GCType.FULL_GC_CMS));
            }
            appData.put("full_gc_info", full_gc_list.isEmpty() ? null : full_gc_list);
            String appKey = GCUtil.getAppKey(file);
            orderMap.put(
                    jvmInfo.getMinorGCTimes() == 0 ? 0 : totalRunTime
                            / (jvmInfo.getMinorGCTimes() * 1000), appKey);
            report.put(appKey, appData);
        }
        // 根据GC周期排序
        Map<String, Object> newReport = new LinkedHashMap<String, Object>();
        for (Entry<Long, String> entry : orderMap.entrySet()) {
            newReport.put(entry.getValue(), report.get(entry.getValue()));
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("report", newReport);
        data.put("date", date);
        if (report.isEmpty()) {
            logger.warn("no data found,report will not be sent!");
            return report;
        }
        // send mail
        //sendMailService.send("GCReport", data);
        return newReport;
    }
}
