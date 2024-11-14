package com.k4ln.debug4j;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.k4ln.debug4j.compile.CompilerUtil;
import com.k4ln.debug4j.compile.JavaSourceCompiler;
import com.k4ln.debug4j.compile.ResourceClassLoader;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

@Slf4j
public class ReTransformAgentmain {

    public static void agentmain(String agentArgs, Instrumentation inst) {
        log.info("ReTransform agent agentmain run with hutool:{}", RandomUtil.randomNumbers(8));
        inst.addTransformer(new CusDefinedClass(), true);
        for (Class allLoadedClass : inst.getAllLoadedClasses()) {
            if (allLoadedClass.getName().contains("com.k4ln.demo.Demo1Main")) {
                try {
                    inst.retransformClasses(allLoadedClass);
                } catch (UnmodifiableClassException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // 【无需重启-在线修改代码】: https://segmentfault.com/a/1190000040027690?sort=votes
    static class CusDefinedClass implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
            log.info(String.format("tid=%s, pid=%s, agentmain transform: %s", Thread.currentThread().getId(), ProcessHandle.current().pid(), className));
            try {
                if (!className.contains("com/k4ln/demo/Demo1Main")) {
                    return classfileBuffer;
                }
                // java动态编译：CompilerUtil -> web3动态函数（web3sd[test]）
                // jar动态加载：ClassLoaderUtil
                // koTime | arthas retransform

                // 方式一：IDEA 编译生成 Demo1Main.class[retransform/Demo1Main.class2更名] 读取字节码
//                return Files.readAllBytes(Paths.get("E:\\Demo1Main.class"));

                // 方式二：动态编译源码 Demo1Main.java[retransform/Demo1Main.java2更名] 读取字节码
//                JavaSourceCompiler javaSourceCompiler = CompilerUtil.getCompiler(loader)
//                        // 被编译的源码文件
//                        .addSource(FileUtil.file("E:\\Demo1Main.java"));
                // 由于双亲委派机制，新加载的类在jvm中并不会被加载
//                ResourceClassLoader classLoader = (ResourceClassLoader) javaSourceCompiler.compile();

//                Map resourceMap = classLoader.getResourceMap();
//                Resource resource = (Resource) resourceMap.get("com.k4ln.demo.Demo1Main");
//                return resource.readBytes();

                // 方式三：尝试不重写hutool<CompilerUtil>读取class字节码【失败】
//                JavaSourceCompiler javaSourceCompiler = CompilerUtil.getCompiler(loader) //与传入null结果一致
//                        // 被编译的源码文件
//                        .addSource(FileUtil.file("E:\\Demo1Main2.java"));
//
//                ResourceClassLoader classLoader = (ResourceClassLoader) javaSourceCompiler.compile();

                // Hutool生成的.class未设置Path，仅会存在与内存中，不会写入classpath，因此无法读取
//                InputStream resourceAsStream = classLoader.getResourceAsStream("/com/k4ln/demo/Demo1Main2.class");
//                if (resourceAsStream != null) {
//                    return resourceAsStream.readAllBytes();
//                }

                // 系统加载的class读取正常
//                InputStream resourceAsStreamMain = classLoader.getResourceAsStream("com/k4ln/demo/Demo1Main.class");
//                if (resourceAsStreamMain != null) {
//                    return resourceAsStreamMain.readAllBytes();
//                }

            } catch (Exception e) {
                e.printStackTrace();
//                throw new RuntimeException(e);
            }
            return classfileBuffer;
        }
    }

}
