package com.meituan.reflect;

public class Main {
	public static void main(String[] args) {
	    DynamicProxy dp = new DynamicProxy();
	    ITest test = (ITest) dp.bind(new Test());
	    test.test1();
	    test.test2();
    }
}
