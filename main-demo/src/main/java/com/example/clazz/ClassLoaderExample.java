package com.example.clazz;

public class ClassLoaderExample {
    public static void main(String[] args) throws ClassNotFoundException {
        // Class.forName() 方法可以获取原生类型的 Class，而 ClassLoader.loadClass() 则会报错。
        Class<?> x = Class.forName("[I");
        System.out.println(x);

        // Class<?> x2 = ClassLoader.getSystemClassLoader().loadClass("[I");
        Class<?> x3 = ClassLoader.getSystemClassLoader().loadClass("java.lang.String");
        System.out.println(x3);

    }
}
