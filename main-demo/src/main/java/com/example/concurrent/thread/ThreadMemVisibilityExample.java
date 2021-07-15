package com.example.concurrent.thread;

/**
 * <p> 验证多线程的内存可见性以及原子性
 * <p> COUNT_MAX越大离期望值偏差越大， 主要受线程启动的时间差影响(COUNT_MAX太小，有可能第一个线程已执行完，第二个线程才启动)
 */
public class ThreadMemVisibilityExample {
    private long count = 0;
    static final long COUNT_MAX = 10000;

    //对count的值累加1000次
    private void addCount(){
        for(int i = 0; i < COUNT_MAX; i++){
            count ++;
        }
    }

    public long execute() throws InterruptedException {
        //创建两个线程，执行count的累加操作
        Thread threadA = new Thread(() ->{
            addCount();
        });
        Thread threadB = new Thread(() ->{
            addCount();
        });
        //启动线程
        threadA.start();
        threadB.start();
        //等待线程执行结束
        threadA.join();
        threadB.join();
        //返回结果
        return count;
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadMemVisibilityExample testThread = new ThreadMemVisibilityExample();
        long count = testThread.execute();
        System.out.println(count);
    }

}
