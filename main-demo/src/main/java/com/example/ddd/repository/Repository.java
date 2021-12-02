package com.example.ddd.repository;

import java.io.Serializable;

public interface Repository<T extends Aggregate<ID>, ID extends Identifier> {
    // 可追踪
    void attach(T aggregate);

    // 接触追踪
    void detach(T aggregate);

    T find(ID id);

    void remove(T aggregate);

    void save(T aggregate);
}



interface Entity<ID extends Identifier> extends Identifiable<ID> {

}

interface Identifiable<ID extends Identifier> {
    ID getId();
}





