package com.k4ln.debug4j.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
