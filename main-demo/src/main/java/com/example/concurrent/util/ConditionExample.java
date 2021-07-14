package com.example.concurrent.util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Condition是一个多线程间协调通信的工具类，Condition除了实现wait和notify的功能以外，它的好处在于一个lock可以创建多个
 * Condition，可以选择性的通知wait的线程
 */
public class ConditionExample {
    public static void main(String[] args) throws InterruptedException {
        ReentrantLock reentrantLock = new ReentrantLock();
        Condition condition = reentrantLock.newCondition();

        // could new multiple condition
        // Condition condition2 = reentrantLock.newCondition();
        new Thread(() -> {
            try {
                reentrantLock.lock();
                System.out.println("wait signal"); // 1
                // 从AQS队列中移除，并释放锁，同时进入condition队列等待
                condition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("get signal"); // 4
            reentrantLock.unlock();
        }).start();

        // make thread1 execution wait at condition
        Thread.sleep(1000);
        new Thread(() -> {
            reentrantLock.lock();
            System.out.println("get lock"); // 2
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 唤醒condition队列中等待的线程节点，唤醒的节点加入到AQS中准备争抢锁
            condition.signalAll();
            System.out.println("send signal ~ "); // 3
            reentrantLock.unlock();
        }).start();
    }
}
