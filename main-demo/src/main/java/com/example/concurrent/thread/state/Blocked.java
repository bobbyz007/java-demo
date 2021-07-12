package com.example.concurrent.thread.state;

import java.util.concurrent.TimeUnit;

/**
 * @author binghe
 * @version 1.0.0
 * @description 加锁后不再释放锁
 */
public class Blocked implements Runnable {
    @Override
    public void run() {
        synchronized (Blocked.class){
            while (true){
                waitSecond(100);
            }
        }
    }

    // 线程等待多少秒
    public static final void waitSecond(long seconds){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
