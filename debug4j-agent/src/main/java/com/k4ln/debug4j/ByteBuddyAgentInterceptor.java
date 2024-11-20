package com.k4ln.debug4j;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class ByteBuddyAgentInterceptor {

    @RuntimeType
    public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) throws Exception {
        long start = System.nanoTime();
        try {
            // 原有函数执行
            return callable.call();
        } finally {
            System.out.println(method + " 方法耗时： " + (System.nanoTime() - start) + "ns");
        }
    }

    // 获取原方法信息和参数
//    public static Object intercept(@This Object instance, @AllArguments Object[] args, @Origin Method method) {
//        System.out.println("原方法: " + method.getName());
//        System.out.println("原方法所在类的实例: " + instance);
//        System.out.println("原方法参数: " + Arrays.toString(args));
//
//        return null; // 或者调用原方法的逻辑
//    }

}
