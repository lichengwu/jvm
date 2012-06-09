/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package oliver.jvm.analyse;

import java.util.Date;

/**
 * JVM Info
 *
 * @author lichengwu
 * @created 2011-11-19
 *
 * @version 1.0
 */
public class JvmInfo {
    
    public JvmInfo(String name){
        this.name = name;
    }
    
    private String name;

    /**
     * 启动时间
     */
    private Date beginTime;
    
    /**
     * 关闭时间
     */
    private Date endTime;
    
    /**
     * 永久代大小
     */
    private Long permGenSize;
    
    /**
     * 伊甸园大小
     */
    private Long edenSize;
    
    /**
     * Survivor From 大小
     */
    private Long fromSize;
    
    /**
     * Survivor To 大小
     */
    private Long toSize;
    
    /**
     * 新生代大小
     */
    private Long yongGenSize;
    
    /**
     * 堆大小
     */
    private Long heapSize;
    
    /**
     * 老年代大小
     */
    private Long oldGenSize;
    
    /**
     * 总GC次数
     */
    private Integer totalGCTimes;
    
    /**
     * Full GC次数
     */
    private Integer fullGCTims;
    
    /**
     * Minor FC 次数
     */
    private Integer minorGCTimes;
    
    /**
     * 其他参数
     */
    private String info;

    /**
     * @return the beginTime
     */
    public Date getBeginTime() {
        return beginTime;
    }

    /**
     * @param beginTime
     */
    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the permGenSize
     */
    public Long getPermGenSize() {
        return permGenSize;
    }

    /**
     * @param permGenSize
     */
    public void setPermGenSize(Long permGenSize) {
        this.permGenSize = permGenSize;
    }

    /**
     * @return the edenSize
     */
    public Long getEdenSize() {
        return edenSize;
    }

    /**
     * @param edenSize
     */
    public void setEdenSize(Long edenSize) {
        this.edenSize = edenSize;
    }

    /**
     * @return the fromSize
     */
    public Long getFromSize() {
        return fromSize;
    }

    /**
     * @param fromSize
     */
    public void setFromSize(Long fromSize) {
        this.fromSize = fromSize;
    }

    /**
     * @return the toSize
     */
    public Long getToSize() {
        return toSize;
    }

    /**
     * @param toSize
     */
    public void setToSize(Long toSize) {
        this.toSize = toSize;
    }

    /**
     * @return the yongGenSize
     */
    public Long getYongGenSize() {
        return yongGenSize;
    }

    /**
     * @param yongGenSize
     */
    public void setYongGenSize(Long yongGenSize) {
        this.yongGenSize = yongGenSize;
    }

    /**
     * @return the heapSize
     */
    public Long getHeapSize() {
        return heapSize;
    }

    /**
     * @param heapSize
     */
    public void setHeapSize(Long heapSize) {
        this.heapSize = heapSize;
    }

    /**
     * @return the oldGenSize
     */
    public Long getOldGenSize() {
        return oldGenSize;
    }

    /**
     * @param oldGenSize
     */
    public void setOldGenSize(Long oldGenSize) {
        this.oldGenSize = oldGenSize;
    }

    /**
     * @return the totalGCTimes
     */
    public Integer getTotalGCTimes() {
        return totalGCTimes;
    }

    /**
     * @param totalGCTimes
     */
    public void setTotalGCTimes(Integer totalGCTimes) {
        this.totalGCTimes = totalGCTimes;
    }

    /**
     * @return the fullGCTims
     */
    public Integer getFullGCTims() {
        return fullGCTims;
    }

    /**
     * @param fullGCTims
     */
    public void setFullGCTims(Integer fullGCTims) {
        this.fullGCTims = fullGCTims;
    }

    /**
     * @return the minorGCTimes
     */
    public Integer getMinorGCTimes() {
        return minorGCTimes;
    }

    /**
     * @param minorGCTimes
     */
    public void setMinorGCTimes(Integer minorGCTimes) {
        this.minorGCTimes = minorGCTimes;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("JvmInfo [beginTime=").append(beginTime).append(", endTime=")
                .append(endTime).append(", permGenSize=").append(permGenSize).append("K, edenSize=")
                .append(edenSize).append("K, fromSize=").append(fromSize).append("K, toSize=")
                .append(toSize).append("K, yongGenSize=").append(yongGenSize).append("K, heapSize=")
                .append(heapSize).append("K, oldGenSize=").append(oldGenSize)
                .append("K, totalGCTimes=").append(totalGCTimes).append(", fullGCTims=")
                .append(fullGCTims).append(", minorGCTimes=").append(minorGCTimes).append("]");
        return builder.toString();
    }

    /**
     * @return the info
     */
    public String getInfo() {
        return info;
    }

    /**
     * @param info
     */
    public void setInfo(String info) {
        this.info = info;
    }

    
}
