package com.example.concurrent.util;

import com.google.common.base.Stopwatch;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * <p>作用：ReadWriteLock读写锁的一个改进版本，带来吞吐量的提升。
 *
 * <p>读写锁虽然分离了读和写的功能,使得读与读之间可以完全并发,但是读和写之间依然是冲突的,读锁会完全阻塞写锁,它使用的依然是悲观的锁策略.
 * 如果有大量的读线程,他也有可能引起写线程的饥饿。
 *
 * <p>StampedLock读的过程中也允许获取写锁后写入！这样一来，我们读的数据就可能不一致，所以，需要一点额外的代码来判断读的过程中是否有写入，
 * 这种读锁是一种乐观锁。乐观锁的意思就是乐观地估计读的过程中大概率不会有写入，因此被称为乐观锁。
 * 反过来，悲观锁则是读的过程中拒绝有写入，也就是写入必须等待。显然乐观锁的并发效率更高，但一旦有小概率的写入导致读取的数据不一致，需要能检测出来，再读一遍就行。
 *
 * <p>使用限制：1. 不可重入，意味着你应该始终确保锁以及对应的门票不要逃逸出所在的代码块； 2. 不支持条件变量
 */
public class StampedLockExample {
    private double x, y;
    private final StampedLock sl = new StampedLock();

    public StampedLockExample(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // an exclusively locked method
    void move(double deltaX, double deltaY) {
        // 加写锁
        long stamp = sl.writeLock();
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            // 释放写锁
            sl.unlockWrite(stamp);
        }
    }

    // a read-only method
    // upgrade from optimistic read to read lock
    // 乐观读锁 升级为 悲观读锁
    double distanceFromOrigin() {
        // 注意：此处是乐观读，不是加乐观读锁
        long stamp = sl.tryOptimisticRead();
        try {
            retryHoldingLock: for (;; stamp = sl.readLock()) {
                if (stamp == 0L)
                    continue retryHoldingLock;
                // possibly racy reads
                // 读的过程中x，y有可能被其他线程修改，比如x，y原来分别为100,200，此时currentX，currentY有可能读到300,400(被其他写线程修改)
                double currentX = x;
                double currentY = y;
                // 调用validate判断是否有写入，如果有，则返回for循环获取悲观读锁
                if (!sl.validate(stamp))
                    continue retryHoldingLock;
                // 如果没有，此时读的结果是正确的
                return Math.hypot(currentX, currentY);
            }
        } finally {
            // 释放悲观读锁
            if (StampedLock.isReadLockStamp(stamp))
                sl.unlockRead(stamp);
        }
    }

    // upgrade from optimistic read to write lock
    // 乐观读锁 升级为 写锁
    void moveIfAtOrigin(double newX, double newY) {
        long stamp = sl.tryOptimisticRead();
        try {
            retryHoldingLock: for (;; stamp = sl.writeLock()) {
                if (stamp == 0L)
                    continue retryHoldingLock;
                // possibly racy reads
                double currentX = x;
                double currentY = y;
                // 判断是否有写线程修改，有的话返回循环获取写锁
                if (!sl.validate(stamp))
                    continue retryHoldingLock;
                // 根据业务情况判断状态，此时没有写线程，正常应该是初始值
                if (currentX != 0.0 || currentY != 0.0)
                    break;

                // 升级为写锁
                stamp = sl.tryConvertToWriteLock(stamp);
                if (stamp == 0L)
                    continue retryHoldingLock;
                // exclusive access
                x = newX;
                y = newY;
                return;
            }
        } finally {
            if (StampedLock.isWriteLockStamp(stamp))
                sl.unlockWrite(stamp);
        }
    }

    // Upgrade read lock to write lock
    // 读锁升级为写锁
    void moveIfAtOrigin2(double newX, double newY) {
        long stamp = sl.readLock();
        try {
            while (x == 0.0 && y == 0.0) {
                // 如果stamp已经是写锁，则直接返回该stamp
                long ws = sl.tryConvertToWriteLock(stamp);
                // 升级为写锁成功
                if (ws != 0L) {
                    stamp = ws;
                    x = newX;
                    y = newY;
                    break;
                }
                // 升级为写锁失败
                else {
                    // 释放原读锁
                    sl.unlockRead(stamp);
                    // 加写锁
                    stamp = sl.writeLock();
                }
            }
        } finally {
            sl.unlock(stamp);
        }
    }

    //请求总数
    public static int clientTotal = 5000;
    //同时并发执行的线程数
    public static int threadTotal = 2000;
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final Semaphore semaphore = new Semaphore(threadTotal);
        final CountDownLatch countDownLatch = new CountDownLatch(clientTotal);

        StampedLockExample main = new StampedLockExample(0, 0);

        Stopwatch stopwatch = Stopwatch.createStarted();
        for(int i = 0; i < clientTotal; i++){
            final int cnt = i;
            executorService.execute(() -> {
                try{
                    semaphore.acquire();
                    // 每30个线程有一个写线程，验证StampedLock与普通Lock的性能差异
                    if (cnt % 10 == 0) {
                        // main.move(1.3, 2.5);
                        main.moveWithPessimisticLock(1.3, 2.5);
                        // main.moveWithRWLock(1.3, 2.5);
                    } else {
                        // main.distanceFromOrigin();
                        main.distanceFromOriginWithPessimisticLock();
                        //main.distanceFromOriginWithRWLock();
                    }
                    semaphore.release();
                }catch (Exception e){
                    System.err.println("exception");
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();
        stopwatch.stop();

        System.out.println("time consume: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + " ms");

        executorService.shutdown();
    }

    private static final Lock lock = new ReentrantLock();
    void moveWithPessimisticLock(double deltaX, double deltaY) {
        try {
            lock.lock();
            x += deltaX;
            y += deltaY;
        } finally {
            // 释放写锁
            lock.unlock();
        }
    }

    double distanceFromOriginWithPessimisticLock() {
        try {
            lock.lock();
            double currentX = x;
            double currentY = y;
            return Math.hypot(currentX, currentY);
        } finally {
            // 释放写锁
            lock.unlock();
        }
    }

    private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private static final Lock readLock = readWriteLock.readLock();
    private static final Lock writeLock = readWriteLock.readLock();
    void moveWithRWLock(double deltaX, double deltaY) {
        try {
            writeLock.lock();
            x += deltaX;
            y += deltaY;
        } finally {
            // 释放写锁
            writeLock.unlock();
        }
    }

    double distanceFromOriginWithRWLock() {
        try {
            readLock.lock();
            double currentX = x;
            double currentY = y;
            return Math.hypot(currentX, currentY);
        } finally {
            // 释放写锁
            readLock.unlock();
        }
    }
}
