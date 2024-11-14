package com.k4ln.debug4j;

import cn.hutool.core.util.RandomUtil;
import javassist.*;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

@Slf4j
public class JavassistAgent {

    // 执行两次：https://blog.csdn.net/NEWCIH/article/details/129185402
    public static void premain(String agentArgs, Instrumentation inst) {
        log.info("Javassist agent premain run with hutool:{}", RandomUtil.randomNumbers(8));
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

    // 【Agent】Javassist 增加方法执行耗时：https://www.cnblogs.com/kukuxjx/p/18234327
    static class CusDefinedClass implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
            log.info(String.format("tid=%s, pid=%s, agentmain transform: %s", Thread.currentThread().getId(), ProcessHandle.current().pid(), className));
            try {
                ClassPool pool = ClassPool.getDefault();

                // // 指定路径，否则可能会出现javassist.NotFoundException的问题
                pool.insertClassPath(new LoaderClassPath(loader));

                if (!className.contains("com/k4ln/demo/Demo1Main")) {
                    return classfileBuffer;
                }

                CtClass cc = pool.get(className.replace("/", "."));

                // enhanceMethod（详细打印每一行内每个调用的耗时）
                CtBehavior[] methods = cc.getDeclaredBehaviors();
                for (CtBehavior method : methods) {
                    enhanceMethod(method);
                }

                // enhanceMethod2
//                CtBehavior[] methods = cc.getDeclaredBehaviors();
//                for (CtBehavior m : methods) {
//                    enhanceMethod2(m);
//                }

                return cc.toBytecode();
            } catch (Exception e) {
                e.printStackTrace();
//                throw new RuntimeException(e);
                return classfileBuffer;
            }
        }
    }

    private static void enhanceMethod(CtBehavior method) throws Exception {
        if (method.isEmpty()) {
            return;
        }
        String methodName = method.getName();
        if ("main".equalsIgnoreCase(methodName)) {
            return;
        }

        final StringBuilder source = new StringBuilder();
        // 前置增强: 打入时间戳
        // 保留原有的代码处理逻辑
        source.append("{")
                .append("long start = System.nanoTime();\n") //前置增强: 打入时间戳
                .append("$_ = $proceed($$);\n")              //调用原有代码，类似于method();($$)表示所有的参数
                .append("System.out.print(\"method:[")
                .append(methodName).append("]\");").append("\n")
                .append("System.out.println(\" cost:[\" +(System.nanoTime() - start)+ \"ns]\");") // 后置增强，计算输出方法执行耗时
                .append("}");

        ExprEditor editor = new ExprEditor() {
            @Override
            public void edit(MethodCall methodCall) throws CannotCompileException {
                log.info("edit  method:{} class:{} lineNumber:{}", methodCall.getMethodName(), methodCall.getClassName(), methodCall.getLineNumber());
                methodCall.replace(source.toString()); // 详细打印每一行内每个调用的耗时
            }
        };
        method.instrument(editor);
    }

    private static void enhanceMethod2(CtBehavior method) throws CannotCompileException {
        if (method.isEmpty() ){
            return;
        }
        String methodName = method.getName();
        method.addLocalVariable("start", CtClass.longType);
        method.insertBefore("start = System.currentTimeMillis();");
        method.insertAfter( String.format("System.out.println(\"%s cost: \" + (System.currentTimeMillis() - start) + \"ms\");", methodName) );
    }


}
