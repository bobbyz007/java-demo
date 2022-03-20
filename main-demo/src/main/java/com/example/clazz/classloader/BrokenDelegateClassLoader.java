package com.example.clazz.classloader;

import java.nio.file.Path;

// 通过重载loadClass 破坏双亲委托机制：
// 1. 首先调用自定义类加载器的 findClass 查找， 如果查找不到再委托给双亲。
// 2. 而JDK默认实现是先委托给双亲
public class BrokenDelegateClassLoader extends MyClassLoader{
    public BrokenDelegateClassLoader(ClassLoader parent) {
        super(null, parent);
    }
    public BrokenDelegateClassLoader(String classDir, ClassLoader parent) {
        super(classDir, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> clazz = findLoadedClass(name);

            // can't find from cache
            if (clazz == null) {
                if (name.startsWith("java") || name.startsWith("javax")) {
                    clazz = getSystemClassLoader().loadClass(name);
                } else {
                    clazz = this.findClass(name);
                    if (clazz == null) {
                        if (getParent() != null) {
                            clazz = getParent().loadClass(name);
                        } else {
                            clazz = getSystemClassLoader().loadClass(name);
                        }
                    }
                }
            }

            if (clazz == null) {
                throw new ClassNotFoundException("The class " + name + " not found.");
            }

            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }
    }
}
