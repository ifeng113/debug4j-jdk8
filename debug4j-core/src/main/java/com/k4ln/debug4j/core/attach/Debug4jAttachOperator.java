package com.k4ln.debug4j.core.attach;

import cn.hutool.core.util.StrUtil;
import com.k4ln.debug4j.common.protocol.command.CommandTypeEnum;
import com.k4ln.debug4j.common.utils.FileUtils;
import jadx.api.JadxArgs;
import jadx.api.JadxDecompiler;
import jadx.api.JavaClass;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

public class Debug4jAttachOperator {

    /**
     * 获取所有class名称
     *
     * @param instrumentation
     * @param configPackageName
     * @param packageName
     * @return
     */
    public static List<String> getAllClass(Instrumentation instrumentation, String configPackageName, String packageName) {
        List<String> classes = new ArrayList<>();
        for (Class allLoadedClass : instrumentation.getAllLoadedClasses()) {
            if (!allLoadedClass.getName().contains("$")
                    && (StrUtil.isBlank(packageName) ? allLoadedClass.getName().startsWith(configPackageName) : allLoadedClass.getName().startsWith(packageName))) {
                classes.add(allLoadedClass.getName());
            }
        }
        return classes;
    }

    /**
     * 获取源码
     *
     * @param className
     * @return
     */
    public static String getClassSource(String className) {
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new LoaderClassPath(ClassLoader.getSystemClassLoader()));
        try {
            CtClass cc = pool.get(className);
            File file = new File(FileUtils.createTempDir(), className + ".class");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(cc.toBytecode());
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
            JadxArgs jadxArgs = new JadxArgs();
            jadxArgs.setInputFile(file);
            JadxDecompiler jadx = new JadxDecompiler(jadxArgs);
            jadx.load();
            for (JavaClass cls : jadx.getClasses()) {
                return cls.getCode();
            }
            jadx.close();
            file.deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 源码热更新
     *
     * @param instrumentation
     * @param className
     * @param sourceCode
     */
    public static void sourceReload(Instrumentation instrumentation, String className, String sourceCode){
        Debug4jClassFileTransformer debug4jClassFileTransformer = new Debug4jClassFileTransformer(className,
                CommandTypeEnum.ATTACH_REQ_CLASS_RELOAD_JAVA, sourceCode, null);
        reTransformer(instrumentation, className, debug4jClassFileTransformer);
    }

    /**
     * 字节码热更新
     *
     * @param instrumentation
     * @param className
     * @param byteCOde
     */
    public static void classReload(Instrumentation instrumentation, String className, String byteCOde){
        Debug4jClassFileTransformer debug4jClassFileTransformer = new Debug4jClassFileTransformer(className,
                CommandTypeEnum.ATTACH_REQ_CLASS_RELOAD, null, byteCOde);
        reTransformer(instrumentation, className, debug4jClassFileTransformer);
    }

    /**
     * 重载类
     * @param instrumentation
     * @param className
     * @param debug4jClassFileTransformer
     */
    private static void reTransformer(Instrumentation instrumentation, String className, Debug4jClassFileTransformer debug4jClassFileTransformer) {
        instrumentation.addTransformer(debug4jClassFileTransformer, true);
        for (Class allLoadedClass : instrumentation.getAllLoadedClasses()) {
            if (allLoadedClass.getName().equals(className)) {
                try {
                    instrumentation.retransformClasses(allLoadedClass);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        instrumentation.removeTransformer(debug4jClassFileTransformer);
    }


}
