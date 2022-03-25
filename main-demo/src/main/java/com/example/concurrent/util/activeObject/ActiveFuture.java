package com.example.concurrent.util.activeObject;

import java.util.concurrent.FutureTask;

public class ActiveFuture<V> extends FutureTask<V> {
    public ActiveFuture() {
        super(() -> null);
    }

    public void set(V v) {
        super.set(v);
    }
}
