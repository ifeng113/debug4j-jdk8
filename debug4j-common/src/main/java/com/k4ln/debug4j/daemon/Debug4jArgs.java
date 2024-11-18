package com.k4ln.debug4j.daemon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Debug4jArgs {

    /**
     * 进程ID
     */
    private Long pid;

    /**
     * 线程名
     */
    private String threadName;

    /**
     * 远程调试端口
     */
    private Integer jdwpPort;

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
     * 服务端密钥
     */
    private String key;

    @Override
    public String toString() {
        return "pid=" + pid +
                ",jdwpPort=" + jdwpPort +
                ",application='" + application + '\'' +
                ",host='" + host + '\'' +
                ",port=" + port +
                ",key='" + key + '\'';
    }
}
