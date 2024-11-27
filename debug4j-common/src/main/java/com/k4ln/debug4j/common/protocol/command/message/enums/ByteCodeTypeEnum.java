package com.k4ln.debug4j.common.protocol.command.message.enums;

public enum ByteCodeTypeEnum {
    original,               // 未使用-javaagent修改字节码
    agentOnlyJavassist,     // 使用-javaagent（仅含Javassist）修改字节码
    agentWithByteBuddy,     // 使用-javaagent（含ByteBuddy）修改字节码
}
