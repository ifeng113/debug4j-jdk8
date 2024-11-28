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
public class AttachClassSourceLineReqVO {

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
     * <p>ByteBuddy方式方法名为动态，因此需要先不使用方法名进行源码查询</p>
     */
    private String lineMethodName;

}
