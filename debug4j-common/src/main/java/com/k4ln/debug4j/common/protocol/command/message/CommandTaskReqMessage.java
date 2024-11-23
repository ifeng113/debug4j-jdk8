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
public class CommandTaskReqMessage {

    /**
     * 请求ID
     */
    private String reqId;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 监听超时（分钟）
     */
    private Integer expire;

    public static byte[] buildTaskAllMessage(String reqId) {
        return (JSON.toJSONString(Command.builder()
                .command(CommandTypeEnum.ATTACH_REQ_TASK)
                .data(CommandTaskReqMessage.builder()
                        .reqId(reqId)
                        .build())
                .build())
        ).getBytes();
    }

    public static byte[] buildTaskOpenMessage(String reqId, String filePath, Integer expire) {
        return (JSON.toJSONString(Command.builder()
                .command(CommandTypeEnum.ATTACH_REQ_TASK_OPEN)
                .data(CommandTaskReqMessage.builder()
                        .reqId(reqId)
                        .filePath(filePath)
                        .expire(expire)
                        .build())
                .build())
        ).getBytes();
    }

    public static byte[] buildTaskCloseMessage(String reqId, String filePath) {
        return (JSON.toJSONString(Command.builder()
                .command(CommandTypeEnum.ATTACH_REQ_TASK_CLOSE)
                .data(CommandTaskReqMessage.builder()
                        .reqId(reqId)
                        .filePath(filePath)
                        .build())
                .build())
        ).getBytes();
    }
}
