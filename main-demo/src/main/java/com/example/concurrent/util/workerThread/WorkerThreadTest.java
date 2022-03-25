package com.example.concurrent.util.workerThread;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * <p>Worker Thread 模式实现了调用和执行分离；而Thread Per Message模式也实现了同样的效果；不同的是Thread Per Message对于每个请求会创建一个线程，
 * 而Worker Thread则事先创建了许多线程；相当于对线程进行了缓存和重用；</p>
 *
 * <p>ClientThread相当于Producer，不断地生产Request；然后Channel相当于Table，而WorkerThread则相当于Consumer，不断地处理Request；
 * 不同的是Channel缓存了WorkerThread，相当于把Consumer和Table绑在了一起；从这个角度来看，Worker Thread 和Producer-Consumer模式很类似；
 * 只是角色各有不同：Worker Thread模式中，只出现了两个角色：请求者和缓冲池；而Producer-Consumer模式则有三个角色：Producer-Buffer-Consumer；Buffer是独立的；
 *
 * <p>前面在Thread Per Message模式中，提到“从处理方来看，则没有顺序了，不仅任务结束时无序的，甚至连任务的开始也是无序的”；实际上，这个“无序”是在请求者的角度来看。
 * 从Channel角度来看，只是对顺序没有做处理而已，实际上只要想控制，那么Channel是可以控制“执行”顺序的；因为“调用和执行”是分离的嘛；Client负责调用；Worker负责执行；
 * 而作为Worker的管理者——Channel自然可以对“执行”做出控制！
 *
 * 总结： channel必须知道Worker的存在， 而producer-consumer模式中， queue不用感知consumer的存在
 */
public class WorkerThreadTest {
    public static void main(String[] args) {
        final ProductChannel productChannel = new ProductChannel(5);

        AtomicInteger productNo = new AtomicInteger();

        // 上游有8个工人往流水线上投递待加工的产品
        IntStream.range(0, 8).forEach(i ->{
            new Thread(() -> {
                while (true) {
                    productChannel.offerProduct(new Product(productNo.getAndIncrement()));

                    try {
                        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(10));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }).start();
        });
    }
}
