package com.example.concurrent.thread;

import java.util.ArrayList;
import java.util.List;

/**
 * notify 与 notifyAll 区别
 * <p>
 *  文章中有一个地方说得挺好，就是面试常问的 notify 和 notifyAll 的区别
 *
 * 青铜玩家会一脸纯真的看着面试官，就是唤醒一个和唤醒一堆啊，但它两真正的区别是 notifyAll 调用后，会把所有在 Wait Set 中的线程状态变成 RUNNABLE 状态，
 * 然后这些线程再去竞争锁，获取到锁的线程为 Run 状态，没有获取到锁的线程进入 Entry Set 集合中变成 Block 状态，它们只需要等到上个线程执行完或者
 * wait 就可以再次竞争锁而无需 notify ； 而 notify 方法只是照规则唤醒 Wait Set 中的某一个线程，其它的线程还是在 Wait Set 中。
 *
 * 文章中说到的为什么 wait 要写在 for 循环中是因为 wait 是释放了锁，然后阻塞，等到下次唤醒的时候，在多个生产者多个消费者的情况下，有可能是被 “同类” 唤醒的，
 * 所以需要再去检查下状态是否正确。
 *
 * 文章中有一个地方没有说明白 ，这里再解释下，就是那个使用 notfiy 会带来死锁的问题，个人理解，如有偏差望指正
 * 当有多个消费者和多个生产者的时候，这时正好在消费，所以生产者是在 Wait Set 中，可能还有其它消费者也在 Wait Set 中，因为是 notify 而不是 notfiyAll 嘛，
 * 所以消费者有可能一直 notify 的都是另一个消费者，刚好这时 buffer 空了，正好所有消费都 wait 了而没能及时 notify 生产者，这时 Wait Set 中四目相望造成死锁。
 * </p>
 *
 * <p>
 *     先说两个概念：锁池和等待池
 * 1. 锁池:假设线程A已经拥有了某个对象(注意:不是类)的锁，而其它的线程想要调用这个对象的某个synchronized方法(或者synchronized块)，
 * 由于这些线程在进入对象的synchronized方法之前必须先获得该对象的锁的拥有权，但是该对象的锁目前正被线程A拥有，所以这些线程就进入了该对象的锁池中。
 * 2. 等待池:假设一个线程A调用了某个对象的wait()方法，线程A就会释放该对象的锁后，进入到了该对象的等待池中
 *
 * 然后再来说notify和notifyAll的区别
 *
 * 1. 如果线程调用了对象的 wait()方法，那么线程便会处于该对象的等待池中，等待池中的线程不会去竞争该对象的锁。
 * 2. 当有线程调用了对象的 notifyAll()方法（唤醒所有 wait 线程）或 notify()方法（只随机唤醒一个 wait 线程），被唤醒的的线程便会进入该对象的锁池中，
 * 锁池中的线程会去竞争该对象锁。也就是说，调用了notify后只要一个线程会由等待池进入锁池，而notifyAll会将该对象等待池内的所有线程移动到锁池中，等待锁竞争
 * 3. 优先级高的线程竞争到对象锁的概率大，假若某线程没有竞争到该对象锁，它还会留在锁池中，唯有线程再次调用 wait()方法，它才会重新回到等待池中。
 * 而竞争到对象锁的线程则继续往下执行，直到执行完了 synchronized 代码块，它会释放掉该对象锁，这时锁池中的线程会继续竞争该对象锁。
 * </p>
 *
 * <p>
 * refer：
 * <a href="https://stackoverflow.com/questions/37026/java-notify-vs-notifyall-all-over-again#">stackoverflow</a>
 * <a href="https://copyfuture.com/blogs-details/2019121216503432855fwtdhzqm2impg">resource</a>
 * <a href="https://blog.csdn.net/mmalan/article/details/79598166">resource</a>
 * <a href="https://www.iteye.com/blog/unique5945-1721418">resource</a>
 * </p>
 */
public class WaitNotifyOrNotifyAll {
    private static final int MAX_SIZE = 10;

    private List<Object> buf = new ArrayList<>();

    public synchronized void put(Object o) throws InterruptedException {
        while (buf.size()==MAX_SIZE) {
            wait(); // called if the buffer is full (try/catch removed for brevity)
        }
        buf.add(o);
        // ************** may deadlock **************
        notify(); // called in case there are any getters or putters waiting

        // deadlock-free
        notifyAll();
    }

    public synchronized Object get() throws InterruptedException {
        // Y: this is where C2 tries to acquire the lock (i.e. at the beginning of the method)
        while (buf.size()==0) {
            wait(); // called if the buffer is empty (try/catch removed for brevity)
            // X: this is where C1 tries to re-acquire the lock (see below)
        }
        Object o = buf.remove(0);
        // ************** may deadlock **************
        notify(); // called if there are any getters or putters waiting

        // deadlock-free
        notifyAll();
        return o;
    }

}
