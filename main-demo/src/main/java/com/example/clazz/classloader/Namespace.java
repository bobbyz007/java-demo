package com.example.clazz.classloader;

/**
 * 每一个类加载器都有各自的命名空间
 * 命名空间是由该类加载器及其所有父加载器所构成的。 每个类加载器中的同一个class都是独一无二的。
 */
public class Namespace {
    public static void main(String[] args) throws ClassNotFoundException {
        ClassLoader classLoader = Namespace.class.getClassLoader();

        // 同一个类加载器加载
        Class<?> aClass = classLoader.loadClass("com.example.clazz.classloader.Namespace");
        Class<?> bClass = classLoader.loadClass("com.example.clazz.classloader.Namespace");

        System.out.println(aClass.hashCode());
        System.out.println(bClass.hashCode());
        System.out.println(aClass == bClass);

        // 不同类加载器加载
        System.out.println("-----------------------------------------------------");
        MyClassLoader classLoader1 = new MyClassLoader();
        BrokenDelegateClassLoader classLoader2 = new BrokenDelegateClassLoader(null);
        System.out.println("class loader 1's parent: " + classLoader1.getParent());
        System.out.println("class loader 2's parent: " + classLoader2.getParent());
        aClass = classLoader1.loadClass("HelloWorld");
        bClass = classLoader2.loadClass("HelloWorld");
        System.out.println(aClass.hashCode());
        System.out.println(bClass.hashCode());
        System.out.println(aClass == bClass);

        // 同一个类加载器的不同实例加载
        System.out.println("-----------------------------------------------------");
        BrokenDelegateClassLoader classLoader3 = new BrokenDelegateClassLoader(null);
        BrokenDelegateClassLoader classLoader4 = new BrokenDelegateClassLoader(null);
        aClass = classLoader3.loadClass("HelloWorld");
        bClass = classLoader4.loadClass("HelloWorld");
        System.out.println(aClass.hashCode());
        System.out.println(bClass.hashCode());
        System.out.println(aClass == bClass);

    }
}
