/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.jvm.analyse;

/**
 * AgeVo
 * 
 * @author lichengwu
 * @created 2011-12-12
 * 
 * @version 1.0
 */
public class AgeVo {

    private Long times;

    private Long size;
    
    private Long totleUser;

    public Long addTimes() {
        return ++times;
    }

    /**
     * @return the times
     */
    public Long getTimes() {
        return times;
    }

    /**
     * @param times
     */
    public void setTimes(Long times) {
        this.times = times;
    }

    /**
     * @return the size
     */
    public Long getSize() {
        return size;
    }

    /**
     * @param size
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * @return the totleUser
     */
    public Long getTotleUser() {
        return totleUser;
    }

    /**
     * @param totleUser
     */
    public void setTotleUser(Long totleUser) {
        this.totleUser = totleUser;
    }
}
