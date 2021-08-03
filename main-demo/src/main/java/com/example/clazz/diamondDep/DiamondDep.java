package com.example.clazz.diamondDep;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class DiamondDep {
    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, IllegalAccessException, InstantiationException, MalformedURLException {
        URL v1Url = new File(
        "D:\\workspace\\opensource\\java-demo\\main-demo\\build\\classes\\java\\main\\com\\example\\clazz\\diamondDep\\v1\\").toURI().toURL();
        URL v2Url = new File(
                "D:\\workspace\\opensource\\java-demo\\main-demo\\build\\classes\\java\\main\\com\\example\\clazz\\diamondDep\\v2\\").toURI().toURL();
        URLClassLoader v1 = new URLClassLoader(new URL[]{v1Url});

        // URLClassLoader v2 = new URLClassLoader(new URL[]{v2Url});
        // 即使v2加载 v1的类，仍然是不同的对象
        URLClassLoader v2 = new URLClassLoader(new URL[]{v1Url});

        Class<?> depV1Class = v1.loadClass("com.example.clazz.diamondDep.v1.Dep");
        Object depV1 = depV1Class.getConstructor().newInstance();
        depV1Class.getMethod("print").invoke(depV1);

        Class<?> depV2Class = v2.loadClass("com.example.clazz.diamondDep.v2.Dep");
        Object depV2 = depV2Class.getConstructor().newInstance();
        depV2Class.getMethod("print").invoke(depV2);

        System.out.println(depV1Class.equals(depV2Class));
    }
}
