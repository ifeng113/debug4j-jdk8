package com.k4ln.debug4j;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.*;
import java.security.ProtectionDomain;

@Slf4j
public class DebugAgentmain {

    public static void agentmain(String agentArgs, Instrumentation inst) {
        log.info("agent agentmain run with hutool:{}", RandomUtil.randomNumbers(8));
        inst.addTransformer(new CusDefinedClass(), true);
        for (Class allLoadedClass : inst.getAllLoadedClasses()) {
            if(allLoadedClass.getName().contains("com.k4ln")){
                try {
                    inst.retransformClasses(allLoadedClass);
                } catch (UnmodifiableClassException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class CusDefinedClass implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
            log.info(String.format("tid=%s, agentmain transform: %s", Thread.currentThread().getId(), className));
            return classfileBuffer;
        }
    }
}
