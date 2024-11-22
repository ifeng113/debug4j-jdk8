package com.k4ln.debug4j.common.response.exception.error;


import com.k4ln.debug4j.common.response.HttpResult;
import com.k4ln.debug4j.common.response.exception.BaseException;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *  参数验证异常类
 *
 * @author k4ln
 * @since 2024-04-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ValidateError extends BaseException {

    public ValidateError() {
        super(HttpResult.VALIDATE_ERROR);
    }

    public ValidateError(String message) {
        super(HttpResult.VALIDATE_ERROR, message);
    }

}
