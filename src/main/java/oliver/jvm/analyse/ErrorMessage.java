/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package oliver.jvm.analyse;

import java.util.Date;


/**
 * 错误消息
 * 
 * @author lichengwu
 * @created 2011-12-8
 * 
 * @version 1.0
 */
public class ErrorMessage {
    
    /**
     * 消息内容
     */
    private String msg;
    /**
     * 发生时间
     */
    private Date occurTime;
    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }
    /**
     * @param msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }
    /**
     * @return the occurDate
     */
    /**
     * @return the occurTime
     */
    public Date getOccurTime() {
        return occurTime;
    }
    /**
     * @param occurTime
     */
    public void setOccurTime(Date occurTime) {
        this.occurTime = occurTime;
    }
}
