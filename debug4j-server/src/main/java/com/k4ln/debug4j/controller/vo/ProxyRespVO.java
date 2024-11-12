package com.k4ln.debug4j.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProxyRespVO {

    /**
     * 代理端口
     */
    private Integer proxyPort;

    /**
     * 允许网段，如：192.168.1.1/24 [192.168.1.0 -> 192.168.1.255]
     */
    private List<String> allowNetworks;
}
