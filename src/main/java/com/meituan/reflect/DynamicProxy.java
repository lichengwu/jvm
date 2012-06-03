package com.meituan.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 代理测试
 * 
 * @author lichengwu
 * @created 2012-3-16
 * 
 * @version 1.0
 */
public class DynamicProxy implements InvocationHandler {

	private Object obj;

	public Object bind(Object obj) {
		this.obj = obj;
		return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass()
		        .getInterfaces(), this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println("begin...");
		return method.invoke(obj, args);
	}

}
