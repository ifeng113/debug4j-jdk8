package com.k4ln.debug4j.core.attach;

import cn.hutool.core.util.StrUtil;
import com.k4ln.debug4j.common.protocol.command.CommandTypeEnum;
import com.k4ln.debug4j.common.protocol.command.message.enums.ByteCodeTypeEnum;
import com.k4ln.debug4j.common.protocol.command.message.enums.SourceCodeTypeEnum;
import com.k4ln.debug4j.common.utils.FileUtils;
import com.k4ln.debug4j.core.attach.dto.ByteCodeInfo;
import com.k4ln.debug4j.core.attach.dto.SourceCodeInfo;
import jadx.api.JadxArgs;
import jadx.api.JadxDecompiler;
import jadx.api.JavaClass;
import javassist.ClassPool;
import javassist.CtClass;
import lombok.Getter;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class Debug4jAttachOperator {

    /**
     * className -> ByteCodeInfo
     */
    @Getter
    private static final Map<String, ByteCodeInfo> realByteCodeMap = new ConcurrentHashMap<>();

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
            if (!allLoadedClass.getName().contains("$") // 过滤内部类
                    && (StrUtil.isBlank(packageName) ? allLoadedClass.getName().startsWith(configPackageName) : allLoadedClass.getName().startsWith(packageName))) {
                classes.add(allLoadedClass.getName());
            }
        }
        return classes;
    }

    /**
     * 获取源码
     *
     * @param instrumentation
     * @param className
     * @return
     */
    public static SourceCodeInfo getClassSource(Instrumentation instrumentation, String className, SourceCodeTypeEnum sourceCodeType) {
        ByteCodeInfo byteCodeInfo = getClassByteCodeInfo(instrumentation, className);
        String classSource = jadxByteCodeToSource(className, getClassByteCodeByCache(sourceCodeType, byteCodeInfo));
        return SourceCodeInfo.builder()
                .byteCodeType(byteCodeInfo != null ? byteCodeInfo.getAttachClassByteCodeType() : null)
                .classSource(classSource)
                .build();
    }

    /**
     * 获取原始字节码
     *
     * @param instrumentation
     * @param className
     * @return
     */
    public static ByteCodeInfo getClassByteCodeInfo(Instrumentation instrumentation, String className) {
        ByteCodeInfo byteCodeInfo = realByteCodeMap.get(className);
        if (byteCodeInfo == null) {
            byteCodeInfo = new ByteCodeInfo();
            // originalClassFileByteCode
            try {
                Class<?> clazz = Class.forName(className);
                URL resource = clazz.getClassLoader().getResource(clazz.getName().replace('.', '/') + ".class");
                assert resource != null;
                byte[] bytes = Files.readAllBytes(new File(resource.toURI()).toPath());
                byteCodeInfo.setOriginalClassFileByteCode(bytes);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            // agentTransformClassByteCode
            try {
                ClassPool pool = ClassPool.getDefault();
                CtClass ctClass = pool.getCtClass(className);
                byteCodeInfo.setAgentTransformClassByteCode(ctClass.toBytecode());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            // agentTransformClassBufferByteCode
            try {
                CompletableFuture<ByteCodeInfo> future = new CompletableFuture<>();
                Debug4jClassFileTransformer debug4jClassFileTransformer = new Debug4jClassFileTransformer(className,
                        CommandTypeEnum.ATTACH_REQ_CLASS_SOURCE, null, null, future, byteCodeInfo);
                reTransformer(instrumentation, className, debug4jClassFileTransformer);
                byteCodeInfo = future.get(30, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            realByteCodeMap.put(className, byteCodeInfo);
        }
        return byteCodeInfo;
    }

    /**
     * 获取原始字节码
     *
     * @param sourceCodeType
     * @param byteCodeInfo
     * @return
     */
    private static byte[] getClassByteCodeByCache(SourceCodeTypeEnum sourceCodeType, ByteCodeInfo byteCodeInfo) {
        byte[] byteCode;
        if (sourceCodeType.equals(SourceCodeTypeEnum.originalClassFile)) {
            byteCode = byteCodeInfo.getOriginalClassFileByteCode();
        } else if (sourceCodeType.equals(SourceCodeTypeEnum.agentTransformClassByteCode)) {
            byteCode = byteCodeInfo.getAgentTransformClassByteCode();
        } else if (sourceCodeType.equals(SourceCodeTypeEnum.agentTransformClassBuffer)) {
            byteCode = byteCodeInfo.getAgentTransformClassBufferByteCode();
        } else {
            byteCode = byteCodeInfo.getAttachClassByteCode();
        }
        return byteCode;
    }

    /**
     * jadx字节码转源码
     *
     * @param className
     * @param byteCode
     * @return
     */
    public static String jadxByteCodeToSource(String className, byte[] byteCode) {
        try {
            File file = new File(FileUtils.createTempDir(), className + ".class");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(byteCode);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            JadxArgs jadxArgs = new JadxArgs();
            jadxArgs.setInputFile(file);
            jadxArgs.setDebugInfo(false);
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
        return null;
    }

    /**
     * 源码热更新
     *
     * @param instrumentation
     * @param className
     * @param sourceCode
     */
    public static void sourceReload(Instrumentation instrumentation, String className, String sourceCode) {
        try {
            ByteCodeInfo byteCodeInfo = getClassByteCodeInfo(instrumentation, className);
            if (byteCodeInfo != null && !byteCodeInfo.getAttachClassByteCodeType().equals(ByteCodeTypeEnum.agentWithByteBuddy)) {
                CompletableFuture<ByteCodeInfo> future = new CompletableFuture<>();
                Debug4jClassFileTransformer debug4jClassFileTransformer = new Debug4jClassFileTransformer(className,
                        CommandTypeEnum.ATTACH_REQ_CLASS_RELOAD_JAVA, sourceCode, null, future, byteCodeInfo);
                reTransformer(instrumentation, className, debug4jClassFileTransformer);
                realByteCodeMap.put(className, future.get(30, TimeUnit.SECONDS));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 字节码热更新
     *
     * @param instrumentation
     * @param className
     * @param byteCode
     */
    public static void classReload(Instrumentation instrumentation, String className, String byteCode) {
        try {
            ByteCodeInfo byteCodeInfo = getClassByteCodeInfo(instrumentation, className);
            if (byteCodeInfo != null && !byteCodeInfo.getAttachClassByteCodeType().equals(ByteCodeTypeEnum.agentWithByteBuddy)) {
                CompletableFuture<ByteCodeInfo> future = new CompletableFuture<>();
                Debug4jClassFileTransformer debug4jClassFileTransformer = new Debug4jClassFileTransformer(className,
                        CommandTypeEnum.ATTACH_REQ_CLASS_RELOAD, null, byteCode, future, byteCodeInfo);
                reTransformer(instrumentation, className, debug4jClassFileTransformer);
                realByteCodeMap.put(className, future.get(30, TimeUnit.SECONDS));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 代码还原
     *
     * @param instrumentation
     * @param className
     */
    public static void classRestore(Instrumentation instrumentation, String className) {
        try {
            ByteCodeInfo byteCodeInfo = getClassByteCodeInfo(instrumentation, className);
            if (byteCodeInfo != null) {
                CompletableFuture<ByteCodeInfo> future = new CompletableFuture<>();
                Debug4jClassFileTransformer debug4jClassFileTransformer = new Debug4jClassFileTransformer(className,
                        CommandTypeEnum.ATTACH_REQ_CLASS_RESTORE, null, null, future, byteCodeInfo);
                reTransformer(instrumentation, className, debug4jClassFileTransformer);
                realByteCodeMap.put(className, future.get(30, TimeUnit.SECONDS));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重载类
     *
     * @param instrumentation
     * @param className
     * @param debug4jClassFileTransformer
     */
    private static void reTransformer(Instrumentation instrumentation, String className, Debug4jClassFileTransformer debug4jClassFileTransformer) throws UnmodifiableClassException {
        instrumentation.addTransformer(debug4jClassFileTransformer, true);
        for (Class allLoadedClass : instrumentation.getAllLoadedClasses()) {
            if (allLoadedClass.getName().equals(className)) {
                try {
                    instrumentation.retransformClasses(allLoadedClass);
                } catch (Exception e) {
                    e.printStackTrace();
                    instrumentation.removeTransformer(debug4jClassFileTransformer);
                    throw e;
                }
                break;
            }
        }
        instrumentation.removeTransformer(debug4jClassFileTransformer);
    }

}
