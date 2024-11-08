package com.k4ln.debug4j.protocol;

import lombok.Getter;
import lombok.Setter;

public enum ProtocolTypeEnum {

    HEART("0x0000", "心跳"),

    AUTH("0x0001", "鉴权"),

    TEXT("0x0002", "文本"),


    PROXY("0x0010", "代理"),

    ;

    @Setter
    @Getter
    private String code;

    @Setter
    @Getter
    private String message;

    ProtocolTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 通过code获取协议类型
     *
     * @param code
     * @return
     */
    public static ProtocolTypeEnum getProtocolTypeByCode(String code) {
        ProtocolTypeEnum[] values = ProtocolTypeEnum.values();
        for (ProtocolTypeEnum value : values) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
