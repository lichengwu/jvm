/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package cn.lichengwu.jvm.jvm.gc;

import cn.lichengwu.utils.collection.CollectionUtil;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ReflectionException;


/**
 * 获得垃圾回收器的名字
 * JVM args: -XX:+UseConcMarkSweepGC
 *
 * @author lichengwu
 * @created 2012-1-8
 *
 * @version 1.0
 */
public class ObtainGarbageCollectorName {

	public static void main(String[] args) throws MalformedObjectNameException, NullPointerException, AttributeNotFoundException, InstanceNotFoundException, MBeanException, ReflectionException {
//		System.gc();
	    List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
	    for(GarbageCollectorMXBean gc : garbageCollectorMXBeans){
	    	System.out.println(gc.getName());
	    	System.out.println(gc.getCollectionTime());
	    	System.out.println(gc.getCollectionCount());
	    	System.out.println(CollectionUtil.toString(gc.getMemoryPoolNames(), ","));
	    }
//		List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
//		MBeanServer platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
//	    for(GarbageCollectorMXBean gc : garbageCollectorMXBeans){
//	    	ObjectName ob = new ObjectName("java.lang:type=GarbageCollector,name="+gc.getName());
//	    	System.out.println(platformMBeanServer.getAttribute(ob, "LastGcInfo").toString());
//	    }
//		
    }
	
	
}
