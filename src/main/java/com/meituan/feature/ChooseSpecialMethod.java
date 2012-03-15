package com.meituan.feature;

import java.io.Serializable;

/**
 * http://geekexplains.blogspot.com/2009/06/choosing-most-specific-method-tricky.html
 *
 * @author lichengwu
 * @created 2012-3-15
 *
 * @version 1.0
 */
public class ChooseSpecialMethod {

	public void test(Object obj){
		System.out.println("test object");
	}
	public void test(String c){
		System.out.println("test string");
	}
	public void test(Serializable s){
		System.out.println("test serializable");
	}
	
	public static void main(String[] args) {
	    ChooseSpecialMethod csm = new ChooseSpecialMethod();
	    csm.test(null);
    }
}
