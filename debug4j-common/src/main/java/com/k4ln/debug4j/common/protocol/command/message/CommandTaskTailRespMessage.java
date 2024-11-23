package com.k4ln.debug4j.common.protocol.command.message;

import com.alibaba.fastjson2.JSON;
import com.k4ln.debug4j.common.protocol.command.Command;
import com.k4ln.debug4j.common.protocol.command.CommandTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandTaskTailRespMessage {

    /**
     * 请求ID
     */
    private String filePath;

    /**
     * 内容行
     */
    private String line;

    public static byte[] buildTaskTailRespMessage(String filePath, String line) {
        return (JSON.toJSONString(Command.builder()
                .command(CommandTypeEnum.ATTACH_RESP_TASK_DETAILS)
                .data(CommandTaskTailRespMessage.builder()
                        .filePath(filePath)
                        .line(line)
                        .build())
                .build())
        ).getBytes();
    }
}
