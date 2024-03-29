package com.example.clazz.classloader;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// 自定义类加载器
public class MyClassLoader extends ClassLoader {
    public static final Path DEFAULT_CLASS_PATH;
    private final Path classDir;

    static {
        try {
            DEFAULT_CLASS_PATH = new ClassPathResource("class").getFile().toPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
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

    static void misc() {
        System.out.println("current classloader: " + Thread.currentThread().getContextClassLoader());

        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        ClassLoader platformClassLoader = ClassLoader.getPlatformClassLoader();
        System.out.println("system: " + systemClassLoader);
        System.out.println("platform: " + platformClassLoader);

        System.out.println("system: " + MyClassLoader.class.getClassLoader());
    }

    static void loadPrimitiveType() throws ClassNotFoundException {
        // Class.forName() 方法可以获取原生类型的 Class，而 ClassLoader.loadClass() 则会报错。
        Class<?> x = Class.forName("[I");
        System.out.println(x);

        // Class<?> x2 = ClassLoader.getSystemClassLoader().loadClass("[I");
        Class<?> x3 = ClassLoader.getSystemClassLoader().loadClass("java.lang.String");
        System.out.println(x3);
    }

    public static void main(String[] args) throws ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        MyClassLoader classLoader = new MyClassLoader();
        /**
         *package com.example;
         *
         * public class HelloWorld {
         *     public String welcome() {
         *         return "hello world";
         *     }
         * }
         */
        Class<?> clazz = classLoader.loadClass("com.example.HelloWorld");

        System.out.println("class loader: " + clazz.getClassLoader());

        // clazz.newInstance() 已经deprecated，因为这个方法没有捕捉构造方法抛出的异常

        //以下所有代码注释掉后，则HelloWorld类并不会初始化，因为loadClass只是在加载阶段，并没有主动初始化。
        Object helloWorld = clazz.getDeclaredConstructor().newInstance();
        System.out.println("hello world obj: " + helloWorld);

        Method welcomeMethod = clazz.getMethod("welcome");
        String result = (String) welcomeMethod.invoke(helloWorld);
        System.out.println("result: " + result);

        loadPrimitiveType();

        misc();
    }
}
