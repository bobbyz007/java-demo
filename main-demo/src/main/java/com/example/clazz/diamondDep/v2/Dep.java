package com.example.clazz.diamondDep.v2;

/**
 * 演示不同的ClassLoader加载同样类，解决钻石依赖问题。即依赖的同一类库的允许存在两个不同版本
 */
public class Dep {
    public void print() {
        System.out.println("v1");
    }
}
