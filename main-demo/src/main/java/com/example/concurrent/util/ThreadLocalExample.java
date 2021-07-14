package com.example.concurrent.util;

/**
 * 1. 父子线程继承问题
 * 2. threadlocal变量的remove问题，设计内存泄漏
 */
public class ThreadLocalExample {
    // 变量在父子线程可继承
    private static ThreadLocal<String> threadLocal = new InheritableThreadLocal<String>();
    public static void main(String[] args){
            //在主线程中设置值
        threadLocal.set("ThreadLocalTest");
        //在子线程中获取值
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    System.out.println("子线程获取值：" + threadLocal.get());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        //启动子线程
        thread.start();
        //在主线程中获取值
        System.out.println("主线程获取值：" + threadLocal.get());

        // 不用了记得remove，防止如果线程一直执行，threadlocal变量又不用造成内存泄漏
        // 注意：删除的时候不会级联删除
        threadLocal.remove();

        System.out.println("主线程remove");
        System.out.println("主线程获取值：" + threadLocal.get());
    }
}
