package com.k4ln.debug4j.protocol.command;

import lombok.Getter;
import lombok.Setter;

public enum CommandTypeEnum {

    LOG("0x0000", "日志"),

    INFO("0x0001", "信息"),

    PROXY_OPEN("0x0011", "开启代理"),
    PROXY_CLOSE("0x0012", "关闭代理"),

    ;

    @Setter
    @Getter
    private String code;

    @Setter
    @Getter
    private String message;

    CommandTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 通过code获取协议类型
     *
     * @param code
     * @return
     */
    public static CommandTypeEnum getProtocolTypeByCode(String code) {
        CommandTypeEnum[] values = CommandTypeEnum.values();
        for (CommandTypeEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
