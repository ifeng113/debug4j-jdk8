package com.k4ln.debug4j.controller.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachSourceReloadReqVO {

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
     * 源码
     */
    @NotBlank
    private String sourceCode;

}