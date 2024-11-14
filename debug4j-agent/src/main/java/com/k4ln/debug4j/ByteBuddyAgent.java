package com.k4ln.debug4j;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

@Slf4j
public class ByteBuddyAgent {

    // 执行两次：https://blog.csdn.net/NEWCIH/article/details/129185402
    public static void premain(String agentArgs, Instrumentation inst) {
        log.info("ByteBuddy agent premain run with hutool:{}", RandomUtil.randomNumbers(8));
        AgentBuilder.Transformer transformer = (builder, typeDescription, classLoader, javaModule, protectionDomain) -> {
            return builder
                    .method(ElementMatchers.any()) // 拦截任意方法
                    .intercept(MethodDelegation.to(ByteBuddyAgentInterceptor.class)); // 委托
        };
        new AgentBuilder
                .Default()
                .type(ElementMatchers.nameEndsWith("Demo1Main")) // 指定需要拦截的类
                .transform(transformer)
                .installOn(inst);
    }

}
