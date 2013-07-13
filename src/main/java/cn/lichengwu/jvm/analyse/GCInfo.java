/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.jvm.analyse;

import java.io.Serializable;

/**
 * GC信息
 *
 * @author lichengwu
 * @created 2011-11-18
 *
 * @version 1.0
 */
public class GCInfo implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 5847690368478960283L;

    /**
     * GC类型
     */
    private GCType type;
    
    /**
     * GC总时间
     */
    private Double totalTime;
    
    /**
     * 持久代耗时
     */
    private Double permTime;
    
    /**
     * 新生代耗时
     */
    private Double yongTime;
    
    /**
     * 用户CPU消耗
     */
    private Double userCPU;
    
    /**
     * 系统消耗CPU
     */
    private Double sysCPU;
    
    /**
     * GC发生相对时间
     */
    private Double occurTime; 
    
    /**
     * GC前可用heap大小
     */
    private Long beforeGCHeapAvailableSize;
    
    /**
     * GC后可用heap大小
     */
    private Long afterGCHeapAvailableSize;
    
    /**
     * GC前可用持久代大小
     */
    private Long beforeGCPermAvailableSize;
    
    /**
     * GC后可用持久代大小
     */
    private Long afterGCPermAvailableSize;
    
    /**
     * GC前可用新生代大小
     */
    private Long beforeGCYoungAvailableSize;
    
    /**
     * GC后可用新生代大小
     */
    private Long afterGCYoungAvailableSize;

    /**
     * @return the type
     */
    public GCType getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(GCType type) {
        this.type = type;
    }

    /**
     * @return the totalTime
     */
    public Double getTotalTime() {
        return totalTime;
    }

    /**
     * @param totalTime
     */
    public void setTotalTime(Double totalTime) {
        this.totalTime = totalTime;
    }

    /**
     * @return the permTime
     */
    public Double getPermTime() {
        return permTime;
    }

    /**
     * @param permTime
     */
    public void setPermTime(Double permTime) {
        this.permTime = permTime;
    }

    /**
     * @return the yongTime
     */
    public Double getYongTime() {
        return yongTime;
    }

    /**
     * @param yongTime
     */
    public void setYongTime(Double yongTime) {
        this.yongTime = yongTime;
    }

    /**
     * @return the userCPU
     */
    public Double getUserCPU() {
        return userCPU;
    }

    /**
     * @param userCPU
     */
    public void setUserCPU(Double userCPU) {
        this.userCPU = userCPU;
    }

    /**
     * @return the sysCPU
     */
    public Double getSysCPU() {
        return sysCPU;
    }

    /**
     * @param sysCPU
     */
    public void setSysCPU(Double sysCPU) {
        this.sysCPU = sysCPU;
    }

    /**
     * @return the occurTime
     */
    public Double getOccurTime() {
        return occurTime;
    }

    /**
     * @param occurTime
     */
    public void setOccurTime(Double occurTime) {
        this.occurTime = occurTime;
    }

    /**
     * @return the beforeGCHeapAvailableSize
     */
    public Long getBeforeGCHeapAvailableSize() {
        return beforeGCHeapAvailableSize;
    }

    /**
     * @param beforeGCHeapAvailableSize
     */
    public void setBeforeGCHeapAvailableSize(Long beforeGCHeapAvailableSize) {
        this.beforeGCHeapAvailableSize = beforeGCHeapAvailableSize;
    }

    /**
     * @return the afterGCHeapAvailableSize
     */
    public Long getAfterGCHeapAvailableSize() {
        return afterGCHeapAvailableSize;
    }

    /**
     * @param afterGCHeapAvailableSize
     */
    public void setAfterGCHeapAvailableSize(Long afterGCHeapAvailableSize) {
        this.afterGCHeapAvailableSize = afterGCHeapAvailableSize;
    }

    /**
     * @return the beforeGCPermAvailableSize
     */
    public Long getBeforeGCPermAvailableSize() {
        return beforeGCPermAvailableSize;
    }

    /**
     * @param beforeGCPermAvailableSize
     */
    public void setBeforeGCPermAvailableSize(Long beforeGCPermAvailableSize) {
        this.beforeGCPermAvailableSize = beforeGCPermAvailableSize;
    }

    /**
     * @return the afterGCPermAvailableSize
     */
    public Long getAfterGCPermAvailableSize() {
        return afterGCPermAvailableSize;
    }

    /**
     * @param afterGCPermAvailableSize
     */
    public void setAfterGCPermAvailableSize(Long afterGCPermAvailableSize) {
        this.afterGCPermAvailableSize = afterGCPermAvailableSize;
    }

    /**
     * @return the beforeGCYoungAvailableSize
     */
    public Long getBeforeGCYoungAvailableSize() {
        return beforeGCYoungAvailableSize;
    }

    /**
     * @param beforeGCYoungAvailableSize
     */
    public void setBeforeGCYoungAvailableSize(Long beforeGCYoungAvailableSize) {
        this.beforeGCYoungAvailableSize = beforeGCYoungAvailableSize;
    }

    /**
     * @return the afterGCYoungAvailableSize
     */
    public Long getAfterGCYoungAvailableSize() {
        return afterGCYoungAvailableSize;
    }

    /**
     * @param afterGCYoungAvailableSize
     */
    public void setAfterGCYoungAvailableSize(Long afterGCYoungAvailableSize) {
        this.afterGCYoungAvailableSize = afterGCYoungAvailableSize;
    }
    
}
