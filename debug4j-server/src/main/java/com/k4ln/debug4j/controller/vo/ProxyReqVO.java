package com.k4ln.debug4j.controller.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProxyReqVO {

    /**
     * 备注
     */
    private String remark;

    /**
     * 服务器端口
     */
    private Integer serverPort;

    /**
     * 远程客户端
     */
    @NotBlank
    private String socketClient;

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

    /**
     * 允许网段，如：192.168.1.1/24 [192.168.1.0 -> 192.168.1.255]
     */
    private List<String> allowNetworks;
}
