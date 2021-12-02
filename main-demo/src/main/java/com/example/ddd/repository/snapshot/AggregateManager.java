package com.example.ddd.repository.snapshot;

import com.example.ddd.repository.Aggregate;
import com.example.ddd.repository.Identifier;

public interface AggregateManager<T extends Aggregate<ID>, ID extends Identifier> {
    void attach(T aggregate);

    void attach(T aggregate, ID id);

    void detach(T aggregate);

    T find(ID id);

    EntityDiff detectChanges(T aggregate);

    void merge(T aggregate);

    static <ID extends Identifier, T extends Aggregate<ID>> AggregateManager<T,ID> newInstance(Class<T> targetClass) {
        return new ThreadLocalAggregateManager<>(targetClass);
    }
}
