package com.example.concurrent.thread;

/**
 * 理解关键点：Java中每一个对象都有对应的监视器对象，moniterenter指令即保证只有一个线程持有该对象的监视器锁。
 * 同时每一个对象也有一个waitSet，是一个线程集合：当调用对象的wait方法时，会把当前线程加入到该对象的waitSet线程列表中， 同时释放监视器锁，
 * 不会执行任何进一步的指令。
 *
 * wait 方法返回后，需要重新获取监视器锁，才可以继续往下执行。
 *
 * 演示：wait 和 notify 操作过程中的监视器锁的 持有、释放
 */
public class WaitNotifyExample {
    public static void main(String[] args) {
        Object object = new Object();

        new Thread(new Runnable() {
            @Override
            public void run() {

                synchronized (object) {
                    System.out.println("线程1 获取到监视器锁");
                    try {
                        object.wait();
                        // 等待线程2释放监视器锁后，再获取监视器锁后才能继续执行
                        System.out.println("线程1 恢复啦。我为什么这么久才恢复，因为notify方法虽然早就发生了，可是我还要获取锁才能继续执行。");
                    } catch (InterruptedException e) {
                        System.out.println("线程1 wait方法抛出了InterruptedException异常");
                    }
                }
            }
        }, "线程1").start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (object) {
                    System.out.println("线程2 拿到了监视器锁。为什么呢，因为线程1 在 wait 方法的时候会自动释放锁");
                    System.out.println("线程2 执行 notify 操作");
                    object.notify();
                    System.out.println("线程2 执行完了 notify，先休息3秒再说。");
                    try {
                        Thread.sleep(3000);
                        System.out.println("线程2 休息完啦。注意了，调sleep方法和wait方法不一样，不会释放监视器锁");
                    } catch (InterruptedException e) {

                    }
                    System.out.println("线程2 休息够了，结束操作");
                }
            }
        }, "线程2").start();
    }
}
