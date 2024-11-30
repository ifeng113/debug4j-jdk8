package com.k4ln.debug4j.service.aop;


import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.StrUtil;
import com.k4ln.debug4j.common.response.exception.abort.BusinessAbort;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * 增强接口类
 *
 * @author k4ln
 * @since 2024-10-22
 */
@Aspect
@Slf4j
@Component
public class CodeLockAspect {

    /**
     * sessionId + className -> Lock
     */
    private final TimedCache<String, String> reloadLock = CacheUtil.newTimedCache(60 * 1000);

    /**
     * 解析器
     */
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 代码锁切面
     * <p>
     * Build,Execution,Deployment -> Build Tools -> Gradle -> Build and run using(Run test using)
     * 如果修改为 IDEA，可避免调试agent时执行两次premain方法，但会导致在aop中无法获取参数名，从而导致无法使用EvaluationContext动态获取方法参数
     * 如果修改为 Gradle，能够正常获取参数名，但会导致agent的premain方法执行两次 // todo（在此模式下Javassist premain的inst无法获取主类信息？）
     * </p>
     *
     * @param pjp
     * @return
     */
    @Around("@annotation(com.k4ln.debug4j.service.aop.CodeLock)")
    @SneakyThrows
    public Object auditAround(ProceedingJoinPoint pjp) {
        Object proceed;
        try {
            Signature signature = pjp.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            EvaluationContext context = new StandardEvaluationContext();
            String[] params = getParameterNames(method);
            for (int len = 0; len < params.length; len++) {
                context.setVariable(params[len], pjp.getArgs()[len]);
            }
            CodeLock codeLock = method.getAnnotation(CodeLock.class);
            Expression clientSessionIdExpression = parser.parseExpression(codeLock.clientSessionId());
            String clientSessionId = clientSessionIdExpression.getValue(context, String.class);
            Expression classNameExpression = parser.parseExpression(codeLock.className());
            String className = classNameExpression.getValue(context, String.class);
            if (StrUtil.isNotBlank(clientSessionId) && StrUtil.isNotBlank(className)) {
                codeUpdateLock(clientSessionId, className);
                proceed = pjp.proceed(pjp.getArgs());
                codeUpdateUnLock(clientSessionId, className);
                return proceed;
            } else {
                throw new BusinessAbort("code lock error with clientSessionId:" + clientSessionId + " className:" + className);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessAbort("code lock error:" + e.getMessage());
        }
    }

    /**
     * 代码更新上锁
     *
     * @param clientSessionId
     * @param className
     */
    private synchronized void codeUpdateLock(String clientSessionId, String className) {
        String lock = reloadLock.get(clientSessionId + "@" + className);
        if (lock == null) {
            reloadLock.put(clientSessionId + "@" + className, className);
        } else {
            throw new BusinessAbort("code update locked, please try again later");
        }
    }

    /**
     * 代码更新释放锁
     *
     * @param clientSessionId
     * @param className
     */
    private synchronized void codeUpdateUnLock(String clientSessionId, String className) {
        String lock = reloadLock.get(clientSessionId + "@" + className);
        if (lock != null) {
            reloadLock.remove(clientSessionId + "@" + className);
        }
    }

    /**
     * 获取方法参数名
     * 或使用#{DefaultParameterNameDiscoverer}
     *
     * @param method
     * @return
     */
    private String[] getParameterNames(Method method) {
        List<String> parameters = new ArrayList<>();
        Parameter[] methodParameters = method.getParameters();
        for (Parameter parameter : methodParameters) {
            parameters.add(parameter.getName());
        }
        return parameters.toArray(new String[0]);
    }

}
