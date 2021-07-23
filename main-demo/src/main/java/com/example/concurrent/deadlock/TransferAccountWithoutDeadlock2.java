package com.example.concurrent.deadlock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 解决死锁： 破坏请求与保持条件，即一次性申请所需要的所有资源
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
public class TransferAccountWithoutDeadlock2 {
    private String id;
    //账户的余额
    private Integer balance;

    private static ResourcesRequester requester = new ResourcesRequester();

    public TransferAccountWithoutDeadlock2(String id, Integer balance) {
        this.id = id;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return id + "{" + balance + "}";
    }

    //转账操作
    public void transfer(TransferAccountWithoutDeadlock2 target, Integer transferMoney) throws InterruptedException {
        //自旋申请转出账户和转入账户，直到成功
        while(!requester.applyResources(this, target)){
            //循环体为空 或 休眠一会
            Thread.sleep(10);
        }

        try{
            //对转出账户加锁
            synchronized(this){
                //对转入账户加锁
                synchronized(target){
                    if(this.balance >= transferMoney){
                        this.balance -= transferMoney;
                        target.balance += transferMoney;
                        System.out.println(this + ", " + target);
                    }
                }
            }
        }finally{
            //最后释放所有申请的账户资源
            requester.releaseResources(this, target);
        }
    }

    public static void main(String[] args) {
        TransferAccountWithoutDeadlock2 accountA = new TransferAccountWithoutDeadlock2("A", 10);
        TransferAccountWithoutDeadlock2 accountB = new TransferAccountWithoutDeadlock2("B", 20);
        new Thread(() -> {
            try {
                accountA.transfer(accountB, 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                accountB.transfer(accountA, 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
