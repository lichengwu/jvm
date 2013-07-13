/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.jvm.jvm.gc;

import java.util.Properties;

/**
 * 打印所有JVM信息
 *
 * @author lichengwu
 * @created 2012-1-9
 *
 * @version 1.0
 */
public class DescribeJvmPropertites {
	public static void main(String[] args) {
	    Properties properties = System.getProperties();
	    for(Object key :properties.keySet()){
	    	System.out.println(key+"="+properties.getProperty(String.valueOf(key)));
	    }
    }
}
