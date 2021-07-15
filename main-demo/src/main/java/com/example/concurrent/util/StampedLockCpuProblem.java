package com.example.concurrent.util;

import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.StampedLock;

/**
 * 经验证： cpu 100%问题在jdk 11上运行已经不存在此问题。
 *
 * 可能在jdk 8开始版本存在此问题。
 */
public class StampedLockCpuProblem {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Showing stamped problem");
        final StampedLock lock = new StampedLock();
        Thread T1 = new Thread(() -> {
            //获取写锁
            lock.writeLock();
            //永远阻塞在此处，不释放写锁
            LockSupport.park();
        }
        );
        T1.start();
        //保证T1获取写锁
        Thread.sleep(100);


        Thread T2 = new Thread(() ->
                //阻塞在悲观读锁
                lock.readLock()
        );
        T2.start();
        //保证T2阻塞在读锁
        Thread.sleep(100);

        //中断线程T2
        //会导致线程T2所在CPU飙升
        T2.interrupt();
        T2.join();
    }
}
