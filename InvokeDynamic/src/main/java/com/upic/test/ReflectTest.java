package com.upic.test;

import java.lang.reflect.Method;

public class ReflectTest {
	public static void main(String[] args) throws Exception {
		Class<?> clazz = Class.forName("com.upic.test.po.ClassA");
		if(clazz==null){
			throw new NullPointerException();
		}
		Method method = clazz.getMethod("println", String.class);
		Method method1 = clazz.getMethod("testMethod", String.class);
		Object newInstance = clazz.newInstance();
		method.invoke(newInstance, "Hello reflect");
		method1.invoke(newInstance, "Hello reflect Static");
	}
}
