package com.example.clazz.classloader;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * 演示多线程初始化 static变量或代码块， jvm会保证只执行一次。
 */
public class ClassInit {
    // 静态变量和静态代码块是包含在字节码中的clinit方法中执行的
    //  Java类型初始化过程中对static变量的初始化操作依赖于static域和static代码块的前后关系，
    //  static域与static代码块声明的位置关系会导致java编译器生成<clinit>方法字节码。
    //  类型的初始化方法<clinit>只在该类型被加载时才执行，且只执行一次。
    static {
        try {
            System.out.println("The ClassInit static code block will be invoked.");
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // 即使多线程访问，clinit方法保证只会执行一次。 此方法可应用于单例模式中

        // 注意new： Runnable runnable = Demo::new; 相当于如下代码：
        /**
         * Runnable runnable = new Runnable() {
         *     @Override
         *     public void run() {
         *         new Demo();
         *     }
         * };
         */
        IntStream.range(0, 5).forEach(i -> new Thread(ClassInit::new));
    }

}
