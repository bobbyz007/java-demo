package com.example.concurrent.util.activeObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.lang.Thread.currentThread;

/**
 * <p>
 * active objects 设计模式
 *
 * 调用order方法的 是  main线程，执行order方法的 确实 active daemon线程。</p>
 *
 * <p>
 *     Active Object模式是一种异步编程模式。它通过对方法的调用(Method Invocation)与方法的执行(Method Execution)进行解耦(Decoupling)来提高并发性。
 *     若以任务的概念来说，Active Object模式的核心则是它允许任务的提交(相当于对异步方法的调用)和任务的执行(相当于异步方法的真正执行)分离。
 *     这有点类似于System.gc()这个方法：客户端代码调用完gc()后，一个进行垃圾回收的任务被提交,但此时JVM并不一定进行了垃圾回收，
 *     而可能是在gc()方法调用返回后的某段时间才开始执行任务-回收垃圾。我们知道，System.gc()的调用方代码是运行在自己的线程上(通常是main线程派生的子线程)，
 *     而JVM的垃圾回收这个动作则由专门的工作者线程(垃圾回收线程)来执行。换而言之，System.gc()这个方法所代表的动作(其所定义的功能)的调用方法和执行方法是运行在不同的线程中的，
 *     从而提高了并发性。
 * </p>
 *
 * <p>
 *  Active Object模式通过将方法的调用与执行分离，实现了异步编程。有利于提高并发性，从而提高了系统的吞吐率。
 *
 * Active Object模式还有个好处是它可以将任务(MethodRequest)的提交(调用异步烦方法)和任务的执行策略(Execution Policy)分离。
 * 任务的执行策略被封装在Scheduler的实现类之内，因此它对外是“不可见”的，一旦需要变动也不会影响其他代码，从而降低了系统的耦合性。任务的执行策略可以反映以下一些问题。
 *
 * 采用什么顺序去执行任务，如FIFO、LIFO，或者基于任务中包含的信所定的优先级？
 * 多少个任务可以并发执行？
 * 多少个任务可以被排队等待执行？
 * 如果有任务由于系统过载被拒绝，此时哪个任务该被选中作为牺牲品，应用程序该如何被通知到？
 * 任务执行前、执行后需要执行哪些操作？
 * 这意味着，任务的执行顺序可以和任务的提交顺序不同，可以采用单线程也可以采用多线程去执行任务等。
 * 当然，好处的背后总是隐藏着代价，Active Object模式实现异步编程也有其代价。该模式的参与者有6个之多，其实现过程也包含了不少中间的处理：
 * MethodRequest对象的生成、MethodRequest对象的移动(进出缓冲区)、MethodRequest对象的运行调度和线程上下文切换等。这些处理都有其空间和时间的代价。
 * 因此，Active Object模式适合于分解一个比较耗时的任务(如涉及I/O操作的任务)：将任务的发起和执行进行分离，以减少不必要的等待时间。
 *
 * 虽然模式的参与者较多，但正如本章案例的实现代码所展示的，其中大部分的参与者我们可以利用JDK自身提供的类来实现，以节省编码时间。
 * </p>
 *
 * <p>总结： active object模式解耦了任务的调用与执行， 并且可以根据需求定制任务的执行策略 </p>
 *
 */
public class OrderServiceTest {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        OrderService orderService = OrderServiceFactory.toActiveObject(new OrderServiceImpl());

        orderService.order("hello", 123);
        System.out.println("order return immediately");


        Future<String> future = orderService.findOrderDetails(123);
        System.out.println("find return immediately");
        System.out.println("order detail: " + future.get());

        // 堵塞主线程
        currentThread().join();
    }
}
