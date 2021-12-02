package com.example.ddd.repository.snapshot;

import java.util.Iterator;

public class ListDiff extends Diff implements Iterable<Diff>{
    @Override
    public Iterator<Diff> iterator() {
        return null;
    }
}
