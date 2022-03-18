package com.example.clazz.classloader;

/**
 * static的初始化顺序
 */
public class Singleton {
    private static Singleton instance = new Singleton();

    private static int x = 0;
    private static int y;

    static  {
        z = 100;
    }
    private static int z = 0;
    // z未显式初始化， 则会被static代码块初始化
    // private static int z;

    // static的初始化顺序：按定义顺序
    // private static Singleton instance = new Singleton();

    private Singleton() {
        x++;
        y++;
    }

    public static Singleton getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        Singleton singleton = Singleton.getInstance();
        System.out.println(singleton.x);
        System.out.println(singleton.y);
        System.out.println(singleton.z);
    }
}
