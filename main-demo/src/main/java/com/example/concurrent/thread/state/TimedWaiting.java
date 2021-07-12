package com.example.concurrent.thread.state;

import java.util.concurrent.TimeUnit;

/**
 * @author binghe
 * @version 1.0.0
 * @description 线程不断休眠
 */
public class TimedWaiting implements Runnable{
    @Override
    public void run() {
        while (true){
            waitSecond(200);
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