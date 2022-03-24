package com.example.concurrent.thread;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import java.util.concurrent.CountDownLatch;

/**
 * WaitNotifyOrNotifyAll使用notify方法可能导致死锁。 除了使用notifyAll方法（可能有性能开销）解决外，还可以使用两把锁区分开避免死锁
 *
 */
public class WaitNotifyOrNotifyAll2 {
    private static Buffer buffer = new Buffer();

    private static Object producerLock = new Object();
    private static Object consumerLock = new Object();

    static class Producer extends Thread{
        private CountDownLatch countDownLatch;
        public Producer(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
            setName(this.getClass().getSimpleName()+getName());
        }

        @Override
        public void run() {
            synchronized (producerLock) {
                while (buffer.isFull()) {
                    try {
                        producerLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                synchronized (consumerLock) {
                    buffer.add();
                    consumerLock.notify();
                }
            }
            countDownLatch.countDown();
        }
    }

    static class Consumer extends Thread{
        private CountDownLatch countDownLatch;
        public Consumer(CountDownLatch countDownLatch){
            this.countDownLatch = countDownLatch;
            setName(this.getClass().getSimpleName()+getName());
        }
        @Override
        public void run() {
            synchronized (consumerLock){
                while (buffer.isEmpty()) {
                    try {
                        consumerLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                synchronized (producerLock) {
                    buffer.remove();
                    producerLock.notify();
                }
            }
            countDownLatch.countDown();
        }
    }

    private static class Buffer {
        private static final int MAX_CAPACITY = 1;
        private List innerList = new ArrayList<>(MAX_CAPACITY);

        void add() {
            if (isFull()) {
                throw new IndexOutOfBoundsException();
            } else {
                innerList.add(RandomStringUtils.randomNumeric(5));
            }
            System.out.println(Thread.currentThread().toString() + " add " +innerList);

        }

        void remove() {
            if (isEmpty()) {
                throw new IndexOutOfBoundsException();
            } else {
                innerList.remove(MAX_CAPACITY - 1);
            }
            System.out.println(Thread.currentThread().toString() + " remove " +innerList);
        }

        boolean isEmpty() {
            return innerList.isEmpty();
        }

        boolean isFull() {
            return innerList.size() == MAX_CAPACITY;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int j = 0; j < 100 /*1000000*/; j++) {
            CountDownLatch countDownLatch = new CountDownLatch(4);
            for (int i = 0; i < 2; i++) {
                new Producer(countDownLatch).start();
            }

            for (int i = 0; i < 2; i++) {
                new Consumer(countDownLatch).start();
            }
            countDownLatch.await();
            System.out.println("---------"+j+"-----------"+buffer.innerList);
        }


    }
}
