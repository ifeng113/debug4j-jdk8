package com.k4ln.debug4j.core.attach;

import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.io.resource.Resource;
import com.k4ln.debug4j.common.protocol.command.CommandTypeEnum;
import com.k4ln.debug4j.common.utils.FileUtils;
import com.k4ln.debug4j.core.attach.compile.CompilerUtil;
import com.k4ln.debug4j.core.attach.compile.JavaSourceCompiler;
import com.k4ln.debug4j.core.attach.compile.ResourceClassLoader;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Map;

@Slf4j
public class Debug4jClassFileTransformer implements ClassFileTransformer {

    private final String className;

    private final CommandTypeEnum commandType;

    private final String sourceCode;

    private final String byteCode;

    public Debug4jClassFileTransformer(String className, CommandTypeEnum commandType, String sourceCode, String byteCode) {
        this.className = className;
        this.commandType = commandType;
        this.sourceCode = sourceCode;
        this.byteCode = byteCode;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (className.replace("/", ".").equals(this.className)) {
            if (commandType.equals(CommandTypeEnum.ATTACH_REQ_CLASS_RELOAD)) {
                return byteCode.getBytes();
            } else if (commandType.equals(CommandTypeEnum.ATTACH_REQ_CLASS_RELOAD_JAVA)) {
                File file = new File(FileUtils.createTempDir(), className.substring(className.lastIndexOf('.') + 1) + ".java");
                FileWriter.create(file).write(sourceCode);
                JavaSourceCompiler javaSourceCompiler = CompilerUtil.getCompiler(loader)
                        .addSource(file);
                ResourceClassLoader classLoader = (ResourceClassLoader) javaSourceCompiler.compile();
                Map resourceMap = classLoader.getResourceMap();
                Resource resource = (Resource) resourceMap.get(className);
                file.deleteOnExit();
                return resource.readBytes();
            }
        }
        return classfileBuffer;
    }
}
