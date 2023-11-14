package com.example.concurrent.thread.state;

import java.util.concurrent.TimeUnit;

public class DaemonThread {
    public static void main(String[] args) {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    System.out.println("thread running");
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        t.setDaemon(true);
        t.start();

        System.out.println("main end");

        // 主线程和用户线程全部结束，只剩下守护线程， 此时不会等待守护线程执行完成，虚拟机直接退出。

    }
}
