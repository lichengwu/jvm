package com.meituan.jvm.reference;

import java.lang.ref.WeakReference;

import org.junit.Test;

public class ReferenceQueue {

	
	@Test
	public void getQueueInfo() throws InterruptedException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		
		java.lang.ref.ReferenceQueue<StringBuilder> queue = new java.lang.ref.ReferenceQueue<StringBuilder>();
		WeakReference<StringBuilder> bean = new WeakReference<StringBuilder>(new StringBuilder("char_"),queue);
		bean.clear();
		System.gc();
		System.runFinalization();
//		Field field = java.lang.ref.ReferenceQueue.class.getDeclaredField("NULL");
//        field.setAccessible(true);
		Object obj = queue.poll();
		while(obj!=null){
			System.out.println(obj.getClass());
			obj=queue.poll();
		}
	}
}
