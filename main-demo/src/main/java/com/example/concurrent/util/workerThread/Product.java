package com.example.concurrent.util.workerThread;

public class Product {
    private final int id;

    public Product(int id) {
        this.id = id;
    }

    public final void process() {
        System.out.println("Executing creation process for product: " + id);
    }

    @Override
    public String toString() {
        return "Product-" + id;
    }
}