package com.wenge.model.utils;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;

public class InMemoryJavaFileObject extends SimpleJavaFileObject {

    private final String sourceCode;
    private ByteArrayOutputStream byteCodeStream;
    static InMemoryJavaFileObject fileObject = null;

    public InMemoryJavaFileObject(String className, String sourceCode) {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.sourceCode = sourceCode;
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return new OutputStream() {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            @Override
            public void write(int b) throws IOException {
                buffer.write(b);
            }

            @Override
            public void close() throws IOException {
                super.close();
                byte[] byteCode = buffer.toByteArray();
                fileObject.setByteCode(byteCode);
            }
        };
    }


    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return sourceCode;
    }

    // 添加一个方法来获取编译后的字节码
    public byte[] getBytes() {
        if (byteCodeStream != null) {
            return byteCodeStream.toByteArray();
        }
        return null;
    }

    // 添加一个方法来设置编译后的字节码
    public void setByteCode(byte[] byteCode) {
        if (byteCode != null && byteCode.length > 0) {
            byteCodeStream = new ByteArrayOutputStream();
            try {
                byteCodeStream.write(byteCode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        String className = "HelloWorld";
        String sourceCode = "public class HelloWorld {" +
                "    public static void main(String[] args) {" +
                "        System.out.println(\"Hello, World2!\");" +
                "    }" +
                "}";
        //String DDD = "{\n    \"code\": \"public class HelloWorld {\\n    public static void main(String[] args) {\\n        int number = 3214567;\\n        boolean isPrime = isPrime(number);\\n        System.out.println(number + \\\" is prime: \\\" + isPrime);\\n    }\\n\\n    public static boolean isPrime(int n) {\\n        if (n <= 1) {\\n            return false;\\n        }\\n        for (int i = 2; i <= Math.sqrt(n); i++) {\\n            if (n % i == 0) {\\n                return false;\\n            }\\n        }\\n        return true;\\n    }\\n}\"\n}";

        //JSONObject jsonObject = JSON.parseObject(DDD);
        //String sourceCode = jsonObject.getString("code");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.out.println("Cannot find the system Java compiler. Check that your classpath includes tools.jar");
            System.exit(1);
        }

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        fileObject = new InMemoryJavaFileObject(className, sourceCode);

        // 设置编译完成后的字节码处理器
        JavaFileManager fileManager = new ForwardingJavaFileManager<StandardJavaFileManager>(compiler.getStandardFileManager(diagnostics, null, null)) {
            @Override
            public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
                // 返回一个自定义的 JavaFileObject 用于存储编译后的字节码
                return fileObject;
            }
        };

        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(fileObject);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);

        boolean success = task.call();

        if (success) {
            try {
                System.out.println("编译成功");

                // 创建一个自定义类加载器来加载编译后的类
                CustomClassLoader customClassLoader = new CustomClassLoader();

                // 加载 HelloWorld 类
                Class<?> helloWorldClass = customClassLoader.findClass(className);

                // 调用 HelloWorld 类的 main 方法
                Method mainMethod = helloWorldClass.getMethod("main", String[].class);
                mainMethod.invoke(null, (Object) new String[]{});

                System.out.println("HelloWorld 类已成功运行。");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                System.out.println(diagnostic.getMessage(null));
            }
        }
    }
    static class CustomClassLoader extends ClassLoader {
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] classData = fileObject.getBytes();

            if (classData != null) {
                return defineClass(name, classData, 0, classData.length);
            }

            throw new ClassNotFoundException(name);
        }
    }

}

