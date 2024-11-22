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
public class CommandAttachReqMessage {

    /**
     * 请求ID
     */
    private String reqId;

    /**
     * 包名
     */
    private String packageName;

    /**
     * 类名
     */
    private String className;

    /**
     * 行号
     */
    private Integer lineNumber;

    /**
     * 源码
     */
    private String sourceCode;

    /**
     * 编译字节码
     */
    private String byteCode;

    public static byte[] buildClassAllMessage(String reqId, String packageName) {
        return (JSON.toJSONString(Command.builder()
                .command(CommandTypeEnum.ATTACH_REQ_CLASS_ALL)
                .data(CommandAttachReqMessage.builder()
                        .reqId(reqId)
                        .packageName(packageName)
                        .build())
                .build())
        ).getBytes();
    }

    public static byte[] buildClassSourceMessage(String reqId, String className) {
        return (JSON.toJSONString(Command.builder()
                .command(CommandTypeEnum.ATTACH_REQ_CLASS_SOURCE)
                .data(CommandAttachReqMessage.builder()
                        .reqId(reqId)
                        .className(className)
                        .build())
                .build())
        ).getBytes();
    }

}
