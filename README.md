
# jdk7引进的InvokeDynamic

jdk7引进的InvokeDynamic指令是java实现了动态性，包括对jdk8引出的lambda语法可以说是神来之笔。

- **Reflect与InvokeDynamic**
- **Reflect实现调用**
- **MethodHandle调用**
- **CallSite**

-------------------
### 测试目标
    本次测试目标：通过三种方式，分别调用ClassA中的方法并且传入参数运行。
## Reflect与InvokeDynamic
>概念：InvokeDynamic（字节指令）->java.lang.invoke.MethodHandle
>
>MethodHandle是在模拟字节码层次的方法调用, MethodHandles.Lookup上的三个方法findStatic()、findVirtual()、findSpecial()正是为了对应于invokestatic、invokevirtual & invokeinterface和invokespecial这几条字节码指令的执行权限校验行为.
 
>Reflect是代码层面的方法调用，底层细节在使用Reflection API时是不需要关心的。

  长话短说 直接贴代码:
  
    ClassA的代码：
```
    public class ClassA {
	public void println(String s) {
		System.out.println(s);
	}

	public static void testMethod(String s) {
		System.out.println("hello String：" + s);
	}
}

```
说明：提供两个方法，待会会使用不同形式来调用。

## Reflect实现调用

### 快速启动

```
快速启动: mvn compile exec:java -Dexec.mainClass=com.upic.test.ReflectTest
```

```
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

```

## MethodHandle调用

### 快速启动

```
快速启动: mvn compile exec:java -Dexec.mainClass=com.upic.test.MethodHandleTest
```
关键代码:

```
private static MethodHandle getPrintlnMH(Object reveiver) throws Throwable {
		MethodType mt = MethodType.methodType(void.class, String.class);
		return lookup().findVirtual(reveiver.getClass(), "println", mt).bindTo(reveiver);
	}

```

说明：直接通过MethodHandle.lookup()执行绑定目标方法。

## CallSite

### 快速启动

```
快速启动: mvn compile exec:java -Dexec.mainClass=com.upic.test.InvokeDynamicTest

```

关键代码:

```
public static CallSite BootstrapMethod(MethodHandles.Lookup lookup, String name, MethodType mt,Class<?> claess) throws Throwable {
		return new ConstantCallSite(lookup.findStatic(claess, name, mt));
	}

	// 动态描述参数类型
	private static MethodType MT_BootstrapMethod() {
		return MethodType.fromMethodDescriptorString(
				"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/Class;)Ljava/lang/invoke/CallSite;",
				null);
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



```

说明:引导方法是有固定的参数，并且返回值是java.lang.invoke.CallSite对象，这个代表真正要执行的目标方法调用。根据CONSTANT_InvokeDynamic_info常量中提供的信息，虚拟机可以找到并且执行引导方法，从而获得一个CallSite对象，最终调用要执行的目标方法。


这是简单整理的三个小Demo，还可以通过javassist去调用。
后续还会添加更多的demo。

