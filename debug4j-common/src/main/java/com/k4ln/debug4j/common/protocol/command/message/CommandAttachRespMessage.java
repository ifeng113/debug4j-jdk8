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
public class CommandAttachRespMessage {

    /**
     * 请求ID
     */
    private String reqId;

    /**
     * 源码
     */
    private String sourceCode;

    /**
     * 所有类
     */
    private List<String> classNames;

    public static byte[] buildClassAllRespMessage(String reqId, List<String> classNames) {
        return (JSON.toJSONString(Command.builder()
                .command(CommandTypeEnum.ATTACH_RESP_CLASS_ALL)
                .data(CommandAttachRespMessage.builder()
                        .reqId(reqId)
                        .classNames(classNames)
                        .build())
                .build())
        ).getBytes();
    }

    public static byte[] buildClassSourceRespMessage(String reqId, String sourceCode) {
        return (JSON.toJSONString(Command.builder()
                .command(CommandTypeEnum.ATTACH_RESP_CLASS_ALL)
                .data(CommandAttachRespMessage.builder()
                        .reqId(reqId)
                        .sourceCode(sourceCode)
                        .build())
                .build())
        ).getBytes();
    }
}
