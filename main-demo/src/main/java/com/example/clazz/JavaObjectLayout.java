package com.example.clazz;

import org.openjdk.jol.info.ClassLayout;

/**
 * using jol-cli
 */
public class JavaObjectLayout {
    public static void main(String[] args) throws InterruptedException {
        // objectLayout();

        biasedLock();
    }

    static void biasedLock() throws InterruptedException {
        System.out.println(org.openjdk.jol.vm.VM.current().details());

        Object obj = new Object();
        System.out.println(Thread.currentThread());
        System.out.println(ClassLayout.parseInstance(obj).toPrintable());

        lock(obj);

        for (int i = 0; i < 3; i++) {
            new Thread(() -> lock(obj)).start();
        }
    }

    private static void lock(Object obj) {
        synchronized (obj) {
            System.out.println(Thread.currentThread());
            System.out.println(ClassLayout.parseInstance(obj).toPrintable());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static void objectLayout() {
        System.out.println(ClassLayout.parseInstance(new Object()).toPrintable());

        System.out.println(ClassLayout.parseInstance(new int[]{}).toPrintable());

        System.out.println(ClassLayout.parseInstance(new ArtisanTest()).toPrintable());
    }

    // -XX:+UseCompressedOops           默认开启的压缩所有指针
    // -XX:+UseCompressedClassPointers  默认开启的压缩对象头里的类型指针Klass Pointer
    // Oops : Ordinary Object Pointers
    public static class ArtisanTest {
        //8B mark word
        //4B Klass Pointer   如果关闭压缩-XX:-UseCompressedClassPointers或-XX:-UseCompressedOops，则占用8B
        int id;        //4B
        String name;   //4B  如果关闭压缩-XX:-UseCompressedOops，则占用8B
        byte b;        //1B
        Object o;      //4B  如果关闭压缩-XX:-UseCompressedOops，则占用8B
    }
}
