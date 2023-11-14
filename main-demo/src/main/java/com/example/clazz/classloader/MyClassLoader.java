package com.example.clazz.classloader;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// 自定义类加载器
public class MyClassLoader extends ClassLoader {
    public static final Path DEFAULT_CLASS_PATH = Paths.get("/home/justin/workspace/java/tmp");
    private final Path classDir;

    public MyClassLoader() {
        super();
        this.classDir = DEFAULT_CLASS_PATH;
    }

    public MyClassLoader(String classDir, ClassLoader parent) {
        super(parent);
        this.classDir = StringUtils.isBlank(classDir) ? DEFAULT_CLASS_PATH : Paths.get(classDir);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classBytes = this.readClassBytes(name);
        if (ArrayUtils.isEmpty(classBytes)) {
            throw new ClassNotFoundException("Can not load the class " + name);
        }

        return this.defineClass(name, classBytes, 0, classBytes.length);
    }

    private byte[] readClassBytes(String name) throws ClassNotFoundException {
        String classPath = name.replace(".", "/");
        Path classFullPath = classDir.resolve(Paths.get(classPath + ".class"));
        if (!classFullPath.toFile().exists()) {
            throw new ClassNotFoundException("The class " + name + " not found.");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Files.copy(classFullPath, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new ClassNotFoundException("Load the class " + name + " occur error.", e);
        }
    }

    @Override
    public String toString() {
        return "My ClassLoader";
    }

    public static void main(String[] args) throws ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        MyClassLoader classLoader = new MyClassLoader();
        // 加载class
        Class<?> clazz = classLoader.loadClass("HelloWorld");

        System.out.println("class loader: " + clazz.getClassLoader());

        // clazz.newInstance() 已经deprecated，因为这个方法没有捕捉构造方法抛出的异常

        //以下所有代码注释掉后，则HelloWorld类并不会初始化，因为loadClass只是在加载阶段，并没有主动初始化。
        Object helloWorld = clazz.getDeclaredConstructor().newInstance();
        System.out.println("hello world obj: " + helloWorld);

        Method welcomeMethod = clazz.getMethod("welcome");
        String result = (String) welcomeMethod.invoke(helloWorld);
        System.out.println("result: " + result);
    }
}
