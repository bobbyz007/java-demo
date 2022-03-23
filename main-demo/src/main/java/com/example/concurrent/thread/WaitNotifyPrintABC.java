package com.example.concurrent.thread;

import java.util.concurrent.CyclicBarrier;

/**
 * 三个线程循环打印ABC
 */
public class WaitNotifyPrintABC {
    private static Object lockA = new Object();
    private static Object lockB = new Object();
    private static Object lockC = new Object();
    private static int current = 0;

    // 这里只是让三个线程同时开始
    static  CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

    static class ThreadA extends Thread{
        @Override
        public void run() {
            try {
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 10; i++) {
                synchronized (lockA) {
                    if(current % 3 != 0){
                        try {
                            lockA.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    current++;
                    System.out.print("A");
                    synchronized (lockB) {
                        lockB.notify();
                    }
                }
            }

        }
    }

    static class ThreadB extends Thread{
        @Override
        public void run() {
            try {
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 10; i++) {
                synchronized (lockB) {
                    if(current % 3 != 1){
                        try {
                            lockB.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    current++;
                    System.out.print("B");
                    synchronized (lockC) {
                        lockC.notify();
                    }
                }
            }
        }
    }
    static class ThreadC extends Thread{
        @Override
        public void run() {
            try {
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 10; i++) {
                synchronized (lockC) {
                    if(current % 3 != 2){
                        try {
                            lockC.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    current++;
                    System.out.print("C");
                    if(i == 10){
                        System.out.println();
                    }
                    synchronized (lockA) {
                        lockA.notify();
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new ThreadA().start();
        new ThreadB().start();
        new ThreadC().start();
    }
}

