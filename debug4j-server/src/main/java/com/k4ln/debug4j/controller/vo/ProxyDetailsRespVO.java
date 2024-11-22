package com.k4ln.debug4j.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProxyDetailsRespVO {

    /**
     * 备注
     */
    private String remark;

    /**
     * 服务器端口
     */
    private Integer serverPort;

    /**
     * 客户端sessionId
     */
    private String clientSessionId;

    /**
     * 远程主机
     */
    private String remoteHost;

    /**
     * 远程端口
     */
    private Integer remotePort;

    /**
     * 允许网段，如：192.168.1.1/24 [192.168.1.0 -> 192.168.1.255]
     */
    private List<String> allowNetworks;

    /**
     * 客户端IP
     */
    private Set<String> clientOutletIps;
}
