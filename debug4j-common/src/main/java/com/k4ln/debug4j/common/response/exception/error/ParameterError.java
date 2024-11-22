package com.k4ln.debug4j.common.response.exception.error;

import com.k4ln.debug4j.common.response.HttpResult;
import com.k4ln.debug4j.common.response.exception.BaseException;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *  参数异常类
 *
 * @author k4ln
 * @since 2024-04-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ParameterError extends BaseException {

    public ParameterError() {
        super(HttpResult.PARAMETER_ERROR);
    }

    public ParameterError(String message) {
        super(HttpResult.PARAMETER_ERROR, message);
    }

}
