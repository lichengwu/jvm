package com.meituan.jvm.gc;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.List;

/**
 * 动态获得heap信息
 *
 * @author lichengwu
 * @created 2012-1-9
 *
 * @version 1.0
 */
public class DynamicHeapInfo {
	public static void main(String[] args) {
	    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
	    List<double[]> list = new ArrayList<double[]>();
	    System.out.println(memoryMXBean.getHeapMemoryUsage().getUsed());
	    list.add(new double[1024*1024]);
	    System.out.println(memoryMXBean.getHeapMemoryUsage().getUsed());
	    list.add(new double[1024*1024]);
	    System.out.println(memoryMXBean.getHeapMemoryUsage().getUsed());
    }
}
