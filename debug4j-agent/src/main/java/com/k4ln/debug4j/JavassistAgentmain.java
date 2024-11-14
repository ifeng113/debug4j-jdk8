package com.k4ln.debug4j;

import cn.hutool.core.util.RandomUtil;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

@Slf4j
public class JavassistAgentmain {

    public static void agentmain(String agentArgs, Instrumentation inst) {
        log.info("Javassist agent agentmain run with hutool:{}", RandomUtil.randomNumbers(8));
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
                ClassPool pool = ClassPool.getDefault();

                // // 指定路径，否则可能会出现javassist.NotFoundException的问题
                pool.insertClassPath(new LoaderClassPath(loader));

//                ClassLoader classLoader = classBeingRedefined.getClassLoader();
//                pool.insertClassPath(new LoaderClassPath(classLoader));

//                ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
//                pool.insertClassPath(new LoaderClassPath(systemClassLoader));

                if (!className.contains("com/k4ln/demo/Demo1Main")) {
                    return classfileBuffer;
                }

                CtClass cc = pool.get(className.replace("/", "."));

                CtMethod m = cc.getDeclaredMethod("logNumber");
//                m.insertBefore("{ System.out.println(i); }");
//                m.insertBefore("{ log.info(i+\"\"); }");

                m.insertAt(39, "{ log.info(dog.toString()+\" ppp\"); }");

                m.insertAt(40, "{ log.info(com.alibaba.fastjson2.JSON.toJSONString(dog)+\" ccc\"); }");

//                m.insertAt(40, "{ log.info(toJsonString(dog)); }");

                return cc.toBytecode();
            } catch (Exception e) {
                e.printStackTrace();
//                throw new RuntimeException(e);
                return classfileBuffer;
            }
        }
    }

}
