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
public class ProxyRemoveReqVO {

    /**
     * 客户端sessionId
     */
    @NotBlank
    private String clientSessionId;

    /**
     * 远程主机
     */
    @NotBlank
    private String remoteHost;

    /**
     * 远程端口
     */
    @NotNull
    private Integer remotePort;
}
