/*
 * Copyright (c) 2010-2011 lichengwu
 * All rights reserved.
 * 
 */
package com.meituan.jvm.reference;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

import org.junit.Test;

/**
 * VM args:-Xmx2m -Xms2m
 * 
 * @author lichengwu
 * @created 2012-2-11
 * 
 * @version 1.0
 */
public class ReferenceTest {

	@Test
	public void testStrongReference() {
		Bean bean = new Bean("strong_bean", 5);
		System.gc();
		System.runFinalization();
		System.out.println(bean);
	}
	
	@Test
	public void testSoftReference(){
		SoftReference<Bean> bean = new SoftReference<ReferenceTest.Bean>(new Bean("soft_bean", 5));
		System.gc();
		System.runFinalization();
		System.out.println(bean.get());
	}
	
	@Test
	public void testWeakReferenc(){
		WeakReference<Bean> bean = new WeakReference<ReferenceTest.Bean>(new Bean("weak_bean", 5));
		System.gc();
		System.runFinalization();
		System.out.println(bean.get());
	}
	
//	public void testPhantomReference(){
//		PhantomReference<Bean> bean = new PhantomReference<ReferenceTest.Bean>(new Bean("weak_bean", 5),ReferenceQueue.NULL);
//		System.gc();
//		System.runFinalization();
//		System.out.println(bean.get());
//	}

	class Bean {
		private String name;
		private Integer id;

		/**
		 * @param name
		 * @param id
		 */
		public Bean(String name, Integer id) {
			super();
			this.name = name;
			this.id = id;
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
		 * @return the id
		 */
		public Integer getId() {
			return id;
		}

		/**
		 * @param id
		 */
		public void setId(Integer id) {
			this.id = id;
		}

		/**
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
	        StringBuilder builder = new StringBuilder();
	        builder.append("Bean [name=");
	        builder.append(name);
	        builder.append(", id=");
	        builder.append(id);
	        builder.append("]");
	        return builder.toString();
        }

	}
}
