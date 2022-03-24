package com.example.concurrent.util.latch;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LatchTest {
    public static void main(String[] args){
        Latch latch = new CountDownLatch(4);

        new ProgrammerTravel(latch, "Alex", "Bus").start();
        new ProgrammerTravel(latch, "Gavin", "Metro").start();
        new ProgrammerTravel(latch, "Jack", "Bike").start();
        new ProgrammerTravel(latch, "Bobby", "Walking").start();

        try {
            // latch.await();
            // 超时之后还未完成的线程依然在运行，latch没有管理功能。
            latch.await(TimeUnit.SECONDS, 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        System.out.println("all programmers arrived.");
    }
}
