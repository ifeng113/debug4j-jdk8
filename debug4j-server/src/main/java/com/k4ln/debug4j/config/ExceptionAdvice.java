package com.k4ln.debug4j.config;

import cn.dev33.satoken.exception.NotHttpBasicAuthException;
import com.k4ln.debug4j.common.response.Result;
import com.k4ln.debug4j.common.response.exception.BaseException;
import com.k4ln.debug4j.common.response.exception.error.ParameterError;
import com.k4ln.debug4j.common.response.exception.error.ValidateError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * 异常拦截器
 *
 * @author k4ln
 * @since 2024-04-22
 */
@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

    /**
     * 全局异常
     *
     * @param exception
     * @param <T>
     * @return
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public <T> Result<T> exception(Exception exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        log.error(sw.toString());
        return Result.fail();
    }

    /**
     * 请求或参数异常
     *
     * @param exception
     * @param <T>
     * @return
     */
    @ResponseBody
    @ExceptionHandler({BindException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class})
    public <T> Result<T> httpRequestException(Exception exception) {
        return Result.fail(new ParameterError(exception.getMessage()));
    }

    /**
     * 参数验证异常
     *
     * @param exception
     * @param <T>
     * @return
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public <T> Result<T> methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();
        StringBuilder stringBuilder = new StringBuilder();
        if (result.hasErrors()) {
            List<ObjectError> errors = result.getAllErrors();
            errors.forEach(p -> {
                FieldError fieldError = (FieldError) p;
                stringBuilder.append("[");
                stringBuilder.append(fieldError.getField());
                stringBuilder.append("]:");
                stringBuilder.append(fieldError.getDefaultMessage()).append(" ");
            });
        }
        return Result.fail(new ValidateError(stringBuilder.toString()));
    }

    /**
     * 捕获自定义异常
     *
     * @param baseException
     * @param <T>
     * @return
     */
    @ResponseBody
    @ExceptionHandler(BaseException.class)
    public <T> Result<T> baseException(BaseException baseException) {
        return Result.fail(baseException);
    }

}
