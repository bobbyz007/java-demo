package com.example.concurrent.deadlock;

/**
 * 模拟死锁发生：
 *
 * Java stack information for the threads listed above:
 * ===================================================
 * "Thread-0":
 *         at com.example.concurrent.deadlock.TransferAccount.transfer(TransferAccount.java:19)
 *         - waiting to lock <0x0000000712522938> (a com.example.concurrent.deadlock.TransferAccount)
 *         - locked <0x0000000712522928> (a com.example.concurrent.deadlock.TransferAccount)
 *         at com.example.concurrent.deadlock.TransferAccount.lambda$main$0(TransferAccount.java:32)
 *         at com.example.concurrent.deadlock.TransferAccount$$Lambda$1/0x0000000800060840.run(Unknown Source)
 *         at java.lang.Thread.run(java.base@11.0.11/Thread.java:834)
 * "Thread-1":
 *         at com.example.concurrent.deadlock.TransferAccount.transfer(TransferAccount.java:19)
 *         - waiting to lock <0x0000000712522928> (a com.example.concurrent.deadlock.TransferAccount)
 *         - locked <0x0000000712522938> (a com.example.concurrent.deadlock.TransferAccount)
 *         at com.example.concurrent.deadlock.TransferAccount.lambda$main$1(TransferAccount.java:40)
 *         at com.example.concurrent.deadlock.TransferAccount$$Lambda$2/0x0000000800060c40.run(Unknown Source)
 *         at java.lang.Thread.run(java.base@11.0.11/Thread.java:834)
 *
 * Found 1 deadlock.
 */
public class TransferAccount {
    //账户的余额
    private Integer balance;

    public TransferAccount(Integer balance) {
        this.balance = balance;
    }

    //转账操作
    public void transfer(TransferAccount target, Integer transferMoney) throws InterruptedException {
        //对转出账户加锁
        synchronized(this){
            // 等待会模拟线程死锁
            Thread.sleep(100);

            //对转入账户加锁
            synchronized(target){
                if(this.balance >= transferMoney){
                    this.balance -= transferMoney;
                    target.balance += transferMoney;
                }
            }
        }
    }

    public static void main(String[] args) {
        TransferAccount account1 = new TransferAccount(10);
        TransferAccount account2 = new TransferAccount(20);
        new Thread(() -> {
            try {
                account1.transfer(account2, 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                account2.transfer(account1, 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
