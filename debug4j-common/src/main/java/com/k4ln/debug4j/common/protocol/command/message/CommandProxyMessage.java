package com.k4ln.debug4j.common.protocol.command.message;

import com.alibaba.fastjson2.JSON;
import com.k4ln.debug4j.common.protocol.command.Command;
import com.k4ln.debug4j.common.protocol.command.CommandTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandProxyMessage {

    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    public static byte[] buildCommandProxyMessage(CommandTypeEnum commandType, String host, Integer port) {
        return (JSON.toJSONString(Command.builder()
                .command(commandType)
                .data(CommandProxyMessage.builder()
                        .host(host)
                        .port(port)
                        .build())
                .build())
        ).getBytes();
    }

}
