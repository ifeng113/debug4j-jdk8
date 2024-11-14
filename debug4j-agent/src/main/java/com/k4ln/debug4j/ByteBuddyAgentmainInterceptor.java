package com.k4ln.debug4j;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ByteBuddyAgentmainInterceptor {

    public static void intercept(int i) throws Exception {
        try {
//            Dog dog = Dog.builder().name(RandomUtil.randomNumbers(4)).age(i).build();
            Thread.sleep(3000);
            log.info("random tid:{} pid:{} index:{}", Thread.currentThread().getId(), ProcessHandle.current().pid(), i);
//            log.info("random tid:{} pid:{} index:{} dog:{}", Thread.currentThread().getId(), ProcessHandle.current().pid(), i, JSON.toJSONString(dog));
//            log.info("random tid:{} pid:{} index:{} dog:{}", Thread.currentThread().getId(), ProcessHandle.current().pid(), i, dog.toString());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
