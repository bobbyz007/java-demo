package com.example.ddd.repository.snapshot.util;

import com.example.ddd.repository.Aggregate;
import com.example.ddd.repository.Identifier;

public class ReflectionUtils {
    public static <T extends Aggregate<ID>, ID extends Identifier> void writeField(String idField, T aggregate, ID id) {

    }
}
