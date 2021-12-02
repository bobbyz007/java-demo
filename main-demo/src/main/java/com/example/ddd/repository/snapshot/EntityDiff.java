package com.example.ddd.repository.snapshot;

public class EntityDiff {
    static EntityDiff EMPTY = new EntityDiff();


    public boolean isEmpty() {
        return false;
    }

    public boolean isSelfModified() {
        return false;
    }

    public Diff getDiff(String lineItems) {
        return new Diff();
    }
}
