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
public class CommandLogMessage {

    /**
     * 日志内容
     */
    private String content;

    public static byte[] buildCommandLogMessage(String message) {
        return (JSON.toJSONString(Command.builder()
                .command(CommandTypeEnum.LOG)
                .data(CommandLogMessage.builder()
                        .content(message)
                        .build())
                .build())).getBytes();
    }
}
