package com.k4ln.debug4j.core.attach;

import com.k4ln.debug4j.common.protocol.command.CommandTypeEnum;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class Debug4jClassFileTransformer implements ClassFileTransformer {

    private final String className;

    private final CommandTypeEnum commandType;

    public Debug4jClassFileTransformer(String className, CommandTypeEnum commandType) {
        this.className = className;
        this.commandType = commandType;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        return classfileBuffer;
    }
}
