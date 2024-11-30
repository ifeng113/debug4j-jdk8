package com.k4ln.debug4j.common.protocol.command.message.enums;

public enum SourceCodeTypeEnum {
    originalClassFile,              // 原始class文件的反编译源码
    agentTransformClassByteCode,    // 使用-javaagent（含Javassist）启动后的反编译源码
    agentTransformClassBuffer,      // 使用-javaagent（含ByteBuddy）启动后的反编译源码
    attachClassByteCode             // attach之后的字节码的反编译源码（优先级：agentTransformClassBuffer > agentTransformClassByteCode > originalClassFile）
    // 当启动时及包含-javaagent（含ByteBuddy）又包含-javaagent（含Javassist）时：
    // 如果先执行含Javassist再执行含ByteBuddy，那么agentTransformClassBuffer会包含两者修改之后的结果
    // 如果先执行含ByteBuddy再执行含Javassist，因为ByteBuddy执行会改变类的签名和结构（方法名变化等），在大多数情况下Javassist会执行失败（除非使用ClassPool.makeClass(new ByteArrayInputStream(classfileBuffer));）
}
