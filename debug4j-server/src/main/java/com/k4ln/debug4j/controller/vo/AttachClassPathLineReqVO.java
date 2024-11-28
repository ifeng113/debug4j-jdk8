package com.k4ln.debug4j.controller.vo;

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
public class AttachClassPathLineReqVO {

    /**
     * 客户端sessionId
     */
    @NotBlank
    private String clientSessionId;

    /**
     * 类名
     */
    @NotBlank
    private String className;

    /**
     * 行号所在方法名
     */
    @NotBlank
    private String lineMethodName;

    /**
     * 行号
     */
    @NotNull
    private Integer lineNumber;

    /**
     * 补丁代码
     */
    @NotBlank
    private String sourceCode;


}
