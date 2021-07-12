package com.example.concurrent.thread.state;

/**
 * @author binghe
 * @version 1.0.0
 * @description 线程在Warting上等待
 */
public class Waiting implements Runnable {
    @Override
    public void run() {
        while (true){
            synchronized (Waiting.class){
                try {
                    Waiting.class.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
