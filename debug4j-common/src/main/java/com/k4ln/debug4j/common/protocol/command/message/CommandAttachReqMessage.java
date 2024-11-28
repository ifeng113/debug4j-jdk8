package com.k4ln.debug4j.common.protocol.command.message;

import com.alibaba.fastjson2.JSON;
import com.k4ln.debug4j.common.protocol.command.Command;
import com.k4ln.debug4j.common.protocol.command.CommandTypeEnum;
import com.k4ln.debug4j.common.protocol.command.message.enums.SourceCodeTypeEnum;
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
     * 行号所在方法名
     */
    private String lineMethodName;

    /**
     * 源码类型
     */
    private SourceCodeTypeEnum sourceCodeType;

    /**
     * 行号
     */
    private Integer lingNumber;

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

    public static byte[] buildClassSourceMessage(String reqId, String className, SourceCodeTypeEnum sourceCodeType) {
        return (JSON.toJSONString(Command.builder()
                .command(CommandTypeEnum.ATTACH_REQ_CLASS_SOURCE)
                .data(CommandAttachReqMessage.builder()
                        .reqId(reqId)
                        .className(className)
                        .sourceCodeType(sourceCodeType)
                        .build())
                .build())
        ).getBytes();
    }

    public static byte[] buildSourceReloadMessage(String reqId, String className, String sourceCode) {
        return (JSON.toJSONString(Command.builder()
                .command(CommandTypeEnum.ATTACH_REQ_CLASS_RELOAD_JAVA)
                .data(CommandAttachReqMessage.builder()
                        .reqId(reqId)
                        .className(className)
                        .sourceCode(sourceCode)
                        .build())
                .build())
        ).getBytes();
    }

    public static byte[] buildClassReloadMessage(String reqId, String className, String byteCode) {
        return (JSON.toJSONString(Command.builder()
                .command(CommandTypeEnum.ATTACH_REQ_CLASS_RELOAD)
                .data(CommandAttachReqMessage.builder()
                        .reqId(reqId)
                        .className(className)
                        .byteCode(byteCode)
                        .build())
                .build())
        ).getBytes();
    }

    public static byte[] buildClassRestoreMessage(String reqId, String className) {
        return (JSON.toJSONString(Command.builder()
                .command(CommandTypeEnum.ATTACH_REQ_CLASS_RESTORE)
                .data(CommandAttachReqMessage.builder()
                        .reqId(reqId)
                        .className(className)
                        .build())
                .build())
        ).getBytes();
    }

    public static byte[] buildClassSourceMethodLineMessage(String reqId, String className, String lineMethodName) {
        return (JSON.toJSONString(Command.builder()
                .command(CommandTypeEnum.ATTACH_REQ_CLASS_SOURCE_LINE)
                .data(CommandAttachReqMessage.builder()
                        .reqId(reqId)
                        .className(className)
                        .lineMethodName(lineMethodName)
                        .build())
                .build())
        ).getBytes();
    }

    public static byte[] buildClassPatchMethodLineMessage(String reqId, String className, String lineMethodName, String sourceCode, Integer lingNumber) {
        return (JSON.toJSONString(Command.builder()
                .command(CommandTypeEnum.ATTACH_REQ_CLASS_RELOAD_JAVA_LINE)
                .data(CommandAttachReqMessage.builder()
                        .reqId(reqId)
                        .className(className)
                        .lineMethodName(lineMethodName)
                        .sourceCode(sourceCode)
                        .lingNumber(lingNumber)
                        .build())
                .build())
        ).getBytes();
    }
}
