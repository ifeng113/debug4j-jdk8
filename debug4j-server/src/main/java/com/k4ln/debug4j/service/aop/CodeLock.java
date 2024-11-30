package com.k4ln.debug4j.service.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 代码锁
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CodeLock {

    /**
     * 客户端session
     *
     * @return
     */
    String clientSessionId() default "";

    /**
     * 类型名
     *
     * @return
     */
    String className() default "";
}
