package com.k4ln.debug4j.common.response.exception;

import com.k4ln.debug4j.common.response.HttpResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *  自定义异常父类
 *
 * @author k4ln
 * @since 2024-04-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {

    /**
     * 响应码
     */
    private HttpResult result = HttpResult.UNKNOWN_ERROR;

    public BaseException() {
        super(HttpResult.UNKNOWN_ERROR.getMessage());
    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException(HttpResult result) {
        super(result.getMessage());
        this.result = result;
    }

    public BaseException(HttpResult result, String message) {
        super(message);
        this.result = result;
        this.result.setMessage(message);
    }
}
