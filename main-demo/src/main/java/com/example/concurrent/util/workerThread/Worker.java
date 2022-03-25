package com.example.concurrent.util.workerThread;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 流水线上的工人，隶属于流水线管理。 这也是 与 producer-consumer的区别（3个角色: producer, queue, consumer，相互独立）。
 */
public class Worker extends Thread{
    private final ProductChannel productChannel;

    public Worker(String name, ProductChannel channel) {
        super(name);
        this.productChannel = channel;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Product product = productChannel.takeProduct();
                System.out.println(getName() + " process the " + product);
                // product.process();

                TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
