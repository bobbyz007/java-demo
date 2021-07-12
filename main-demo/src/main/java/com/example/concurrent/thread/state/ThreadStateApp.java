package com.example.concurrent.thread.state;

/**
 * 分析各线程所处的状态
 * jps
 * jstack
 */
public class ThreadStateApp {
    public static void main(String[] args){
        new Thread(new TimedWaiting(), "TimedWaitingThread").start();
        new Thread(new Waiting(), "WaitingThread").start();

        //BlockedThread-01线程会抢到锁，BlockedThread-02线程会阻塞
        new Thread(new Blocked(), "BlockedThread-01").start();
        new Thread(new Blocked(), "BlockedThread-02").start();
    }
}
