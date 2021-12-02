package com.example.ddd.persistence;

import com.example.ddd.repository.*;
import com.example.ddd.repository.snapshot.*;

public class OrderRepositoryImpl extends DbRepositorySupport<Order, OrderId> implements OrderRepository {
    protected OrderRepositoryImpl(Class<Order> targetClass) {
        super(targetClass);
    }

    @Override
    public long count(OrderQueryDto queryDto) {
        return 0;
    }

    @Override
    public Page<Order> query(OrderQueryDto queryDto) {
        return null;
    }

    @Override
    public Order findInStore(OrderId id, StoreId storeId) {
        return null;
    }

    @Override
    protected void onInsert(Order aggregate) {

    }

    @Override
    protected Order onSelect(OrderId orderId) {
        return null;
    }

    @Override
    protected void onUpdate(Order aggregate, EntityDiff diff) {
        if (diff.isSelfModified()) {
            /*OrderDO orderDO = converter.toData(aggregate);
            orderDAO.update(orderDO);*/
        }

        Diff lineItemDiffs = diff.getDiff("lineItems");
        if (lineItemDiffs instanceof ListDiff) {
            ListDiff diffList = (ListDiff) lineItemDiffs;
            for (Diff itemDiff : diffList) {
                if (itemDiff.getType() == DiffType.Removed) {
                    LineItem line = (LineItem) itemDiff.getOldValue();
                    /*LineItemDO lineDO = lineItemConverter.toData(line);
                    lineItemDAO.delete(lineDO);*/
                }
                if (itemDiff.getType() == DiffType.Added) {
                    LineItem line = (LineItem) itemDiff.getNewValue();
                    /*LineItemDO lineDO = lineItemConverter.toData(line);
                    lineItemDAO.insert(lineDO);*/
                }
                if (itemDiff.getType() == DiffType.Modified) {
                    LineItem line = (LineItem) itemDiff.getNewValue();
                    /*LineItemDO lineDO = lineItemConverter.toData(line);
                    lineItemDAO.update(lineDO);*/
                }
            }
        }
    }

    @Override
    protected void onDelete(Order aggregate) {

    }
}
