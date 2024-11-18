package com.k4ln.debug4j.boot.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(Debug4jProperties.PREFIX)
public class Debug4jProperties {

    public static final String PREFIX = "debug4j";

    /**
     * 是否开启
     */
    private Boolean enabled = true;

    /**
     * 应用名称
     */
    private String application;

    /**
     * 服务端主机
     */
    private String host;

    /**
     * 服务端端口
     */
    private Integer port;

    /**
     * 通信密钥
     */
    private String key;
}
