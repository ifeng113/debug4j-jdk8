package com.k4ln.debug4j.core.attach;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.util.StrUtil;
import com.k4ln.debug4j.common.protocol.command.CommandTypeEnum;
import com.k4ln.debug4j.common.protocol.command.message.enums.ByteCodeTypeEnum;
import com.k4ln.debug4j.common.utils.FileUtils;
import com.k4ln.debug4j.core.attach.compile.CompilerUtil;
import com.k4ln.debug4j.core.attach.compile.JavaSourceCompiler;
import com.k4ln.debug4j.core.attach.compile.ResourceClassLoader;
import com.k4ln.debug4j.core.attach.dto.ByteCodeInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.k4ln.debug4j.core.attach.Debug4jAttachOperator.jadxByteCodeToSource;

@Slf4j
public class Debug4jClassFileTransformer implements ClassFileTransformer {

    private final String transformerClassName;

    private final CommandTypeEnum commandType;

    private final String sourceCode;

    private final String byteCode;

    private final CompletableFuture<ByteCodeInfo> future;

    private final ByteCodeInfo byteCodeInfo;

    public Debug4jClassFileTransformer(String transformerClassName, CommandTypeEnum commandType, String sourceCode,
                                       String byteCode, CompletableFuture<ByteCodeInfo> future, ByteCodeInfo byteCodeInfo) {
        this.transformerClassName = transformerClassName;
        this.commandType = commandType;
        this.sourceCode = sourceCode;
        this.byteCode = byteCode;
        this.future = future;
        this.byteCodeInfo = byteCodeInfo;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className.replace("/", ".").equals(transformerClassName)) {
            if (commandType.equals(CommandTypeEnum.ATTACH_REQ_CLASS_RELOAD)) {
                byte[] bytes = Base64Decoder.decode(byteCode);
                byteCodeInfo.setAttachClassByteCode(bytes);
                future.complete(byteCodeInfo);
                return bytes;
            } else if (commandType.equals(CommandTypeEnum.ATTACH_REQ_CLASS_RELOAD_JAVA)) {
//                try {
//                    ClassPool pool = ClassPool.getDefault();
////                    pool.makeClass(new ByteArrayInputStream(classfileBuffer));
//                    CtClass cc = pool.get(transformerClassName);
//
////                    CtBehavior[] declaredBehaviors = cc.getDeclaredBehaviors();
////                    for (CtBehavior declaredBehavior : declaredBehaviors) {
////                        log.info("declaredBehavior: {}", declaredBehavior.getName());
////                        if (declaredBehavior.getName().startsWith("logNumber")) {
////
////                            int lineNumber = declaredBehavior.getMethodInfo().getLineNumber(28);
////                            if (lineNumber != -1) {
////
////                                CodeIterator iterator = declaredBehavior.getMethodInfo().getCodeAttribute().iterator();
////                                Set<Integer> set = new HashSet<>();
////                                while (iterator.hasNext()) {
////                                    int next = iterator.next();
////                                    int lineNumber1 = declaredBehavior.getMethodInfo().getLineNumber(next);
////                                    log.info("next: {} lineNumber:{}", next, declaredBehavior.getMethodInfo().getLineNumber(next));
////                                    set.add(lineNumber1);
////                                }
//////                                for (Integer line : set) {
//////                                    declaredBehavior.insertAt(line, "{ log.info(" + line + " +\" ccc\"); }");
//////                                }
////
////                                declaredBehavior.insertBefore("{ log.info(i +\" ccc\"); }");
////
////////                                LineNumberAttribute lineNumberAttribute = (LineNumberAttribute) declaredBehavior.getMethodInfo().getCodeAttribute().getAttribute(LineNumberAttribute.tag);
////////                                for (int i = 0; i < lineNumberAttribute.getConstPool().getSize(); i++) {
////////                                    lineNumberAttribute.getConstPool().print();
////////                                }
////
//////                                declaredBehavior.insertAt(15, "{ log.info(\" ccc15\"); }");
//////                                declaredBehavior.insertAt(lineNumber, "{ log.info(com.alibaba.fastjson2.JSON.toJSONString(dog)+\" ccc\"); }");
////
//////                                CtMethod declaredMethod = cc.getDeclaredMethod(declaredBehavior.getName());
//////                                declaredMethod.insertAt()
////                            }
////                        }
////                    }
//                    // todo insert lineCode
//                    log.info("declaredBehaviors");
//
//                    byte[] bytecode = cc.toBytecode();
////                    String s = jadxByteCodeToSource(transformerClassName, bytecode);
//                    return bytecode;
//                } catch (Exception e){
//                    e.printStackTrace();
//                }

                File file = new File(FileUtils.createTempDir(), transformerClassName.substring(transformerClassName.lastIndexOf('.') + 1) + ".java");
                FileWriter.create(file).write(sourceCode);
                JavaSourceCompiler javaSourceCompiler = CompilerUtil.getCompiler(loader)
                        .addSource(file);
                ResourceClassLoader classLoader = (ResourceClassLoader) javaSourceCompiler.compile();
                Map resourceMap = classLoader.getResourceMap();
                Resource resource = (Resource) resourceMap.get(transformerClassName);
                file.deleteOnExit();
                byte[] bytes = resource.readBytes();
                byteCodeInfo.setAttachClassByteCode(bytes);
                future.complete(byteCodeInfo);
                return bytes;
            } else if (commandType.equals(CommandTypeEnum.ATTACH_REQ_CLASS_SOURCE)) {
                byteCodeInfo.setAgentTransformClassBufferByteCode(classfileBuffer);
                String classFileSourceCode = jadxByteCodeToSource(transformerClassName, byteCodeInfo.getOriginalClassFileByteCode());
                String classBufferSourceCode = jadxByteCodeToSource(transformerClassName, byteCodeInfo.getAgentTransformClassBufferByteCode());
                String classSourceCode = jadxByteCodeToSource(transformerClassName, byteCodeInfo.getAgentTransformClassByteCode());
                if (StrUtil.isBlank(classFileSourceCode) || StrUtil.isBlank(classBufferSourceCode) || StrUtil.isBlank(classSourceCode)) {
                    throw new RuntimeException("class source code is null or empty");
                }
                if (!classFileSourceCode.equals(classBufferSourceCode)) {
                    byteCodeInfo.setAttachClassByteCodeType(ByteCodeTypeEnum.agentWithByteBuddy);
                    byteCodeInfo.setAttachClassByteCode(byteCodeInfo.getAgentTransformClassBufferByteCode());
                } else if (!classFileSourceCode.equals(classSourceCode)) {
                    byteCodeInfo.setAttachClassByteCodeType(ByteCodeTypeEnum.agentOnlyJavassist);
                    byteCodeInfo.setAttachClassByteCode(byteCodeInfo.getAgentTransformClassByteCode());
                } else {
                    byteCodeInfo.setAttachClassByteCodeType(ByteCodeTypeEnum.original);
                    byteCodeInfo.setAttachClassByteCode(byteCodeInfo.getOriginalClassFileByteCode());
                }
                future.complete(byteCodeInfo);
                return byteCodeInfo.getAttachClassByteCode();
            } else if (commandType.equals(CommandTypeEnum.ATTACH_REQ_CLASS_RESTORE)) {
                byte[] defaultByteCode = getDefaultByteCode(byteCodeInfo);
                byteCodeInfo.setAttachClassByteCode(defaultByteCode);
                future.complete(byteCodeInfo);
            }
        }
        return classfileBuffer;
    }

    /**
     * 获取默认字节码
     *
     * @param byteCodeInfo
     * @return
     */
    private byte[] getDefaultByteCode(ByteCodeInfo byteCodeInfo) {
        if (byteCodeInfo.getAttachClassByteCodeType().equals(ByteCodeTypeEnum.agentWithByteBuddy)) {
            return byteCodeInfo.getAgentTransformClassBufferByteCode();
        } else if (byteCodeInfo.getAttachClassByteCodeType().equals(ByteCodeTypeEnum.agentOnlyJavassist)) {
            return byteCodeInfo.getAgentTransformClassByteCode();
        } else {
            return byteCodeInfo.getOriginalClassFileByteCode();
        }
    }

}
