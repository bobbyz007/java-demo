package com.example.clazz.classloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MyClassLoaderTest {
    public static void main(String[] args) throws ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        MyClassLoader classLoader = new MyClassLoader();
        // 加载class
        Class<?> clazz = classLoader.loadClass("HelloWorld");

        System.out.println("class loader: " + clazz.getClassLoader());

        // clazz.newInstance() 已经deprecated，因为这个方法没有捕捉构造方法抛出的异常

        //以下所有代码注释掉后，则HelloWorld类并不会初始化，因为loadClass只是在加载阶段，并没有主动初始化。
        Object helloWorld = clazz.getDeclaredConstructor().newInstance();
        System.out.println("hello world obj: " + helloWorld);

        Method welcomeMethod = clazz.getMethod("welcome");
        String result = (String) welcomeMethod.invoke(helloWorld);
        System.out.println("result: " + result);
    }
}
