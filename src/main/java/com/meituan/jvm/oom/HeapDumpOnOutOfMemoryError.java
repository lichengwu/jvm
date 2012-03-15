/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.jvm.oom;

import java.util.ArrayList;
import java.util.List;

/**
 * VM args:-Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=E:\\jvm_dump
 * 
 * @author lichengwu
 * @created 2012-1-7
 * 
 * @version 1.0
 */
public class HeapDumpOnOutOfMemoryError {
	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		while (true) {
			list.add("oom test");
		}
	}
}
