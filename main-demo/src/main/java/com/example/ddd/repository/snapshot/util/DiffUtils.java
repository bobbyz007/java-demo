package com.example.ddd.repository.snapshot.util;

import com.example.ddd.repository.Aggregate;
import com.example.ddd.repository.Identifier;
import com.example.ddd.repository.snapshot.EntityDiff;

public class DiffUtils {
    public static <T extends Aggregate<ID>, ID extends Identifier> EntityDiff diff(T snapshot, T aggregate) {
        return null;
    }
}
