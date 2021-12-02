package com.example.ddd.repository.snapshot;

import com.example.ddd.repository.Aggregate;
import com.example.ddd.repository.Identifier;
import com.example.ddd.repository.Repository;


public abstract class DbRepositorySupport<T extends Aggregate<ID>, ID extends Identifier> implements Repository<T, ID> {

    private final Class<T> targetClass;

    private AggregateManager<T, ID> aggregateManager;

    protected DbRepositorySupport(Class<T> targetClass) {
        this.targetClass = targetClass;
        this.aggregateManager = AggregateManager.newInstance(targetClass);
    }

    protected abstract void onInsert(T aggregate);
    protected abstract T onSelect(ID id);
    protected abstract void onUpdate(T aggregate, EntityDiff diff);
    protected abstract void onDelete(T aggregate);

    @Override
    public void attach(T aggregate) {
        this.aggregateManager.attach(aggregate);
    }

    @Override
    public void detach(T aggregate) {
        this.aggregateManager.detach(aggregate);
    }

    @Override
    public T find(ID id) {
        T aggregate = this.onSelect(id);
        if (aggregate != null) {
            // 这里的就是让查询出来的对象能够被追踪。
            // 如果自己实现了一个定制查询接口，要记得单独调用 attach。
            this.attach(aggregate);
        }
        return aggregate;
    }

    @Override
    public void remove(T aggregate) {
        this.onDelete(aggregate);
        // 删除停止追踪
        this.detach(aggregate);
    }

    @Override
    public void save(T aggregate) {
        // 如果没有 ID，直接插入
        if (aggregate.getId() == null) {
            this.onInsert(aggregate);
            this.attach(aggregate);
            return;
        }

        // 做 Diff
        EntityDiff diff = aggregateManager.detectChanges(aggregate);
        if (diff.isEmpty()) {
            return;
        }

        // 调用 UPDATE
        this.onUpdate(aggregate, diff);
        // 最终将 DB 带来的变化更新回 AggregateManager
        aggregateManager.merge(aggregate);
    }


}
