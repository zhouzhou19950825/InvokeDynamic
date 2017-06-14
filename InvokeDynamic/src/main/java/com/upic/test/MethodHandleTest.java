package com.upic.test;

import static java.lang.invoke.MethodHandles.lookup;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import com.upic.test.po.ClassA;

/***
 * Method Handle基础用法演示
 * 
 * @author
 */
public class MethodHandleTest {

	public static void main(String[] args) throws Throwable {
		Object obj = System.currentTimeMillis() % 2 == 0 ? System.out : new ClassA();
		getPrintlnMH(obj).invokeExact("Hello MethodHandle");
	}

	private static MethodHandle getPrintlnMH(Object reveiver) throws Throwable {
		MethodType mt = MethodType.methodType(void.class, String.class);
		return lookup().findVirtual(reveiver.getClass(), "println", mt).bindTo(reveiver);
	}
}
