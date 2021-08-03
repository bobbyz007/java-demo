package com.example.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeExample {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        // throws exception:
        // Unsafe unsafe2 = Unsafe.getUnsafe();

        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        unsafe.loadFence();
        System.out.println("unsafe: " + unsafe);
    }
}
