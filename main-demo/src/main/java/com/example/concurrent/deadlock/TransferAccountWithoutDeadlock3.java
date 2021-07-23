package com.example.concurrent.deadlock;

/**
 * 解决死锁： 破坏循坏等待条件，即加锁按照一定的顺序
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
public class TransferAccountWithoutDeadlock3 {
    private String id;
    //账户的余额
    private Integer balance;

    public TransferAccountWithoutDeadlock3(String id, Integer balance) {
        this.id = id;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return id + "{" + balance + "}";
    }

    //转账操作
    public void transfer(TransferAccountWithoutDeadlock3 target, Integer transferMoney) throws InterruptedException {
        TransferAccountWithoutDeadlock3 beforeAccount = this;
        TransferAccountWithoutDeadlock3 afterAccount = target;
        if(this.id.compareTo(target.id) > 0){
            beforeAccount = target;
            afterAccount = this;
        }

        //按顺序加锁，破坏循坏等待条件
        synchronized(beforeAccount) {
            synchronized(afterAccount) {
                if(this.balance >= transferMoney){
                    this.balance -= transferMoney;
                    target.balance += transferMoney;
                    System.out.println(this + ", " + target);
                }
            }
        }
    }

    public static void main(String[] args) {
        TransferAccountWithoutDeadlock3 accountA = new TransferAccountWithoutDeadlock3("A", 10);
        TransferAccountWithoutDeadlock3 accountB = new TransferAccountWithoutDeadlock3("B", 20);
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
