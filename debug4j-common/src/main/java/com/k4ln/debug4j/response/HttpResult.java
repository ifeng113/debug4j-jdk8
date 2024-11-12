package com.k4ln.debug4j.response;

import lombok.Getter;
import lombok.Setter;

/**
 *  响应码
 *
 * @author k4ln
 * @since 2024-04-22
 */
public enum HttpResult {

    // 通用
    SUCCESS("success", 0),
    FAIL("fail", -1),

    // 异常
    UNKNOWN_ERROR("unknown", 1000),
    PARAMETER_ERROR("parameter", 1001),

    VALIDATE_ERROR("validate", 1002),

    // 业务中断
    BUSINESS_ABORT("abort", 2001),

    // 无权限
    NO_AUTH("no auth", 4001);

    // 不能使用 final
    @Setter
    @Getter
    private int code;

    @Setter
    @Getter
    private String message;

    HttpResult(String message, int code) {
        this.code = code;
        this.message = message;
    }
}
