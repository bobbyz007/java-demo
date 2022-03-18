package com.example.concurrent.thread;

import java.util.concurrent.TimeUnit;

// may add multiple hook
public class ThreadHook {
    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("The hook thread 1 is running");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("The hook thread 1 will exit");
        }));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("The hook thread 2 is running");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("The hook thread 2 will exit");
        }));

        System.out.println("The program will stop");
    }
}