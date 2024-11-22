package com.k4ln.debug4j.common.response.exception.abort;


import com.k4ln.debug4j.common.response.HttpResult;
import com.k4ln.debug4j.common.response.exception.BaseException;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *  业务中断异常
 *
 * @author k4ln
 * @since 2024-04-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessAbort extends BaseException {

    public BusinessAbort() {
        super(HttpResult.BUSINESS_ABORT);
    }

    public BusinessAbort(String message) {
        super(HttpResult.BUSINESS_ABORT, message);
    }

}
