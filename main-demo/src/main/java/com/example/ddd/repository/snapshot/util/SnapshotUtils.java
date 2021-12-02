package com.example.ddd.repository.snapshot.util;

import com.example.ddd.repository.Aggregate;
import com.example.ddd.repository.Identifier;

public class SnapshotUtils {
    public static <T extends Aggregate<ID>, ID extends Identifier> T snapshot(T aggregate) {
        return null;
    }
}
