package com.example.concurrent.deadlock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 解决死锁： 破坏不可剥夺条件，核心就是让当前线程自己主动释放占有的资源，有可能一个线程不会转账成功，也有可能两个线程都不会转账成功。
 * 但肯定的是不会发生死锁。
 *
 * 死锁发生的几个必要条件：
 * 1. 互斥条件
 *  在一段时间内某资源仅为一个线程所占有。此时若有其他线程请求该资源，则请求线程只能等待。
 * 2. 不可剥夺条件
 *  线程所获得的资源在未使用完毕之前，不能被其他线程强行夺走，即只能由获得该资源的线程自己来释放（只能是主动释放)。
 * 3. 请求与保持条件
 *  线程已经保持了至少一个资源，但又提出了新的资源请求，而该资源已被其他线程占有，此时请求线程被阻塞，但对自己已获得的
 * 资源保持不放。
 * 4. 循环等待条件
 *  即存在 A等待B，B等待A的情况
 */
public class TransferAccountWithoutDeadlock1 {
    private String id;
    //账户的余额
    private Integer balance;

    private Lock lock = new ReentrantLock();

    public TransferAccountWithoutDeadlock1(String id, Integer balance) {
        this.id = id;
        this.balance = balance;
    }

    private Lock getLock() {
        return lock;
    }

    @Override
    public String toString() {
        return id + "{" + balance + "}";
    }

    //转账操作
    public boolean transfer(TransferAccountWithoutDeadlock1 target, Integer transferMoney) throws InterruptedException {
        boolean thisLocked = this.getLock().tryLock();
        if (thisLocked) {
            try {
                // 模拟竞争锁的情况
                // Thread.sleep(1000);

                boolean targetLocked = target.getLock().tryLock();
                // 如果target加锁失败，则主动释放this锁
                if (targetLocked) {
                    try {
                        if(this.balance >= transferMoney){
                            this.balance -= transferMoney;
                            target.balance += transferMoney;
                            System.out.println(this + ", " + target);
                            return true;
                        }
                    } finally {
                        target.getLock().unlock();
                    }
                }

            } finally {
                // 主动释放this锁
                this.getLock().unlock();
            }
        }
        return false;
    }

    public static void main(String[] args) {
        TransferAccountWithoutDeadlock1 accountA = new TransferAccountWithoutDeadlock1("A", 10);
        TransferAccountWithoutDeadlock1 accountB = new TransferAccountWithoutDeadlock1("B", 20);
        new Thread(() -> {
            try {
                boolean transfered = accountA.transfer(accountB, 5);
                System.out.println("A->B: " + transfered);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                boolean transfered = accountB.transfer(accountA, 5);
                System.out.println("B->A: " + transfered);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
