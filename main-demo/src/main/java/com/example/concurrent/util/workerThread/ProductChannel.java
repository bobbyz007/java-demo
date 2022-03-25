package com.example.concurrent.util.workerThread;

/**
 * Worker thread模式有时称为流水线设计模式。
 *
 * 另外，线程池从某种意义上说也是 worker-thread模式的一种实现。
 */
public class ProductChannel {
    private final static int MAX_PROD = 100;

    private final Product[] productQueue;
    private int tail;
    private int head;

    // 流水线上有多少个产品
    private int total;

    private final Worker[] workers;

    public ProductChannel(int workerCount) {
        this.workers = new Worker[workerCount];
        this.productQueue = new Product[MAX_PROD];

        for (int i = 0; i < workerCount; i++) {
            workers[i] = new Worker("Worker-" + i, this);
            workers[i].start();
        }
    }

    public void offerProduct(Product product) {
        synchronized (this) {
            while (total >= productQueue.length) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            productQueue[tail] = product;
            tail = (tail + 1) % productQueue.length;

            total++;
            notifyAll();
        }
    }

    public Product takeProduct() {
        synchronized (this) {
            while (total <= 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Product product = productQueue[head];
            head = (head + 1) % productQueue.length;

            total--;
            notifyAll();

            return product;
        }
    }
}
