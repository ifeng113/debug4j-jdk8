package com.k4ln.debug4j.controller.vo;

import com.k4ln.debug4j.common.protocol.command.message.enums.ByteCodeTypeEnum;
import com.k4ln.debug4j.common.protocol.command.message.enums.SourceCodeTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachClassSourceRespVO {

    /**
     * 源码
     */
    private String classSource;

    /**
     * 字节码类型
     */
    private ByteCodeTypeEnum byteCodeType;

}
