package com.example.concurrent.util;

import java.util.concurrent.LinkedTransferQueue;

/**
 * LinkedTransferQueue 可以理解为 LinkedBlockingQueue和SynchronousQueue的结合体
 * refer： https://www.jianshu.com/p/ae6977886cec
 */
public class LinkedTransferQueueExample {
    private static final LinkedTransferQueue<String> queue = new LinkedTransferQueue<>();

    public static void main(String[] args) throws InterruptedException {

        Thread t1 = new Thread(() -> {
            try {
                System.out.println("before transfer");
                // transfer: wait
                queue.transfer("bbb");
                // put:not wait
                // queue.put("aaa");
                System.out.println("after waiting another thread to take");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(5000);
                System.out.println("take: " + queue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }
}
