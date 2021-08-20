package com.example.util;

/**
 * 局部性原理
 */
public class LocalityPrinciple {
    public static void main(String[] args) {
        int[][] data = new int[10000][10000];
        for (int i = 0; i < 10000; i++) {
            for (int j = 0; j < 10000; j++) {
                data[i][j] = 1;
            }
        }

        long sum = 0;
        long beginTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            for (int j = 0; j < 10000; j++) {
                sum += data[i][j];
            }
        }
        // 基于数据的就近原则
        System.out.println(String.format("sum = %d cost: %d", sum, System.currentTimeMillis() - beginTime));

        sum = 0;
        beginTime = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            for (int j = 0; j < 10000; j++) {
                sum += data[j][i];
            }
        }
        // 违背局部性原理，性能较差
        System.out.println(String.format("sum = %d cost: %d", sum, System.currentTimeMillis() - beginTime));
    }
}
