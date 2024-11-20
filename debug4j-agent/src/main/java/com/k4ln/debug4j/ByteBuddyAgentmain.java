package com.k4ln.debug4j;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

@Slf4j
public class ByteBuddyAgentmain {

    public static void agentmain(String agentArgs, Instrumentation inst) {
        log.info("ByteBuddy agent agentmain run with hutool:{}", RandomUtil.randomNumbers(8));
        inst.addTransformer(new CusDefinedClass(), true);
        for (Class allLoadedClass : inst.getAllLoadedClasses()) {
            if(allLoadedClass.getName().contains("com.k4ln.demo.Demo1Main")){
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
                if (!className.contains("com/k4ln/demo/Demo1Main")){
                    return classfileBuffer;
                }
                // java动态编译：CompilerUtil -> web3动态函数（web3sd[test]）
                // jar动态加载：ClassLoaderUtil
                // koTime | arthas retransform
                // ByteBuddyAgentmain
                return new ByteBuddy()
                        .redefine(classBeingRedefined)
                        .method(ElementMatchers.named("logNumber"))
                        .intercept(MethodDelegation.to(ByteBuddyAgentmainInterceptor.class))
                        .make()
                        .getBytes();

                // 委托给动态类【未测试】
                // 确保目标类已加载
//                Class<?> targetClass = Class.forName("com.example.ByteBuddyAgentmainInterceptor");
//
//                return new ByteBuddy()
//                        .redefine(classBeingRedefined)
//                        .method(ElementMatchers.named("logNumber"))
//                        .intercept(MethodDelegation.to(targetClass))
//                        .make()
//                        .getBytes();

//                TypePool typePool = TypePool.Default.of(classLoader);
//                TypeDescription interceptorType = typePool.describe("com.example.ByteBuddyAgentmainInterceptor").resolve();
//
//                return new ByteBuddy()
//                        .redefine(classBeingRedefined)
//                        .method(ElementMatchers.named("logNumber"))
//                        .intercept(MethodDelegation.to(interceptorType))
//                        .make()
//                        .getBytes();

            } catch (Exception e) {
                e.printStackTrace();
//                throw new RuntimeException(e);
            }
            return classfileBuffer;
        }
    }

}
