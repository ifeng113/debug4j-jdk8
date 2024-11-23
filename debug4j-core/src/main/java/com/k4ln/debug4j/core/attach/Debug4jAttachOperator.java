package com.k4ln.debug4j.core.attach;

import cn.hutool.core.util.StrUtil;
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
}
