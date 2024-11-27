package com.k4ln.debug4j.core.attach.dto;

import com.k4ln.debug4j.common.protocol.command.message.enums.ByteCodeTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ByteCodeInfo {

    /**
     * 原始class文件的字节码
     */
    private byte[] originalClassFileByteCode;

    /**
     * 使用-javaagent（含Javassist）启动后的字节码
     */
    private byte[] agentTransformClassByteCode;

    /**
     * 使用-javaagent（含ByteBuddy）启动后的字节码
     */
    private byte[] agentTransformClassBufferByteCode;

    /**
     * attach字节码原始类型（标识attachClassByteCode是基于哪种字节码修改，以便还原）
     */
    private ByteCodeTypeEnum attachClassByteCodeType;

    /**
     * attach之后的字节码
     */
    private byte[] attachClassByteCode;
}
