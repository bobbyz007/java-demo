package com.example.concurrent.thread;

import java.util.concurrent.TimeUnit;

// ThreadGroup 实现了 UncaughtExceptionHandler 接口
// 线程发生异常时，会查找本线程的handler，然后默认的handler，然后group实现。
public class EmptyExceptionHandler {
    public static void main(String[] args) {
        ThreadGroup mainGroup = Thread.currentThread().getThreadGroup();

        System.out.println(mainGroup.getName());
        System.out.println(mainGroup.getParent());
        System.out.println(mainGroup.getParent().getParent());

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println(1 / 0);
        }).start();
    }
}
