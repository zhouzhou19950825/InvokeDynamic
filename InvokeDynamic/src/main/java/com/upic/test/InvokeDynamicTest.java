package com.upic.test;

import static java.lang.invoke.MethodHandles.lookup;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import com.upic.test.po.ClassA;
/**
 * 
 * @author 	DTZ
 *
 */
public class InvokeDynamicTest {
	public static void main(String[] args) throws Throwable {
		INDY_BootstrapMethod().invokeExact("Hello CallSite!");
	}

	//引导方法，负责调用目标方法
	public static CallSite BootstrapMethod(MethodHandles.Lookup lookup, String name, MethodType mt,Class<?> claess) throws Throwable {
		return new ConstantCallSite(lookup.findStatic(claess, name, mt));
	}

	// 动态描述参数类型
	private static MethodType MT_BootstrapMethod() {
//		return MethodType.fromMethodDescriptorString(
//				"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)Ljava/lang/invoke/CallSite;",
//				null);
		//第一个参数为返回值
		return MethodType.methodType(CallSite.class,MethodHandles.Lookup.class,String.class,MethodType.class,Class.class);
	}

	// 通过MethodHandle动态加载调用CallSite BootstrapMethod(...)方法
	private static MethodHandle MH_BootstrapMethod() throws Throwable {
		return lookup().findStatic(InvokeDynamicTest.class, "BootstrapMethod", MT_BootstrapMethod());
	}

	private static MethodHandle INDY_BootstrapMethod() throws Throwable {
		CallSite cs = (CallSite) MH_BootstrapMethod().invokeWithArguments(lookup(), "testMethod",
				MethodType.fromMethodDescriptorString("(Ljava/lang/String;)V", null),ClassA.class);
		return cs.dynamicInvoker();
	}
}
