package com.k4ln.debug4j.common.protocol.command;

import lombok.Getter;
import lombok.Setter;

public enum CommandTypeEnum {

    LOG("0x0000", "日志"),

    INFO("0x0001", "信息"),

    PROXY_OPEN("0x0011", "开启代理"),
    PROXY_CLOSE("0x0012", "关闭代理"),

    // server -> client
    ATTACH_REQ_CLASS_ALL("0x0101", "请求所有类"),
    ATTACH_REQ_CLASS_SOURCE("0x0102", "请求类源码"),
    ATTACH_REQ_CLASS_RELOAD("0x0104", "请求类重载"),
    ATTACH_REQ_CLASS_RELOAD_JAVA("0x0105", "请求类编译重载"),
    ATTACH_REQ_CLASS_RESTORE("0x0106", "请求类还原"),

    ATTACH_REQ_TASK("0x0201", "请求任务列表"),
    ATTACH_REQ_TASK_OPEN("0x0202", "请求开启任务"),
    ATTACH_REQ_TASK_CLOSE("0x0203", "请求关闭任务"),

    // client -> server
    ATTACH_RESP_CLASS_ALL("0x0901", "响应所有类"),
    ATTACH_RESP_CLASS_SOURCE("0x0902", "响应类源码"),
    ATTACH_RESP_TASK("0x0903", "响应任务列表"),
    ATTACH_RESP_TASK_DETAILS("0x094", "响应任务详情"),

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
