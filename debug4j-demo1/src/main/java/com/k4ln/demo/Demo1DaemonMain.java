package com.k4ln.demo;

import cn.hutool.core.util.RandomUtil;
import com.k4ln.debug4j.daemon.Debug4jDaemon;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Demo1DaemonMain {

    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {

        Debug4jDaemon.start();

        for (int i = 0; i < 1000; i++) {
            logNumber(i);
            if (i == 999){
                i = 0;
            }
        }
    }

    private static void logNumber(int i) {
        try {
            Dog dog = Dog.builder().name(RandomUtil.randomNumbers(4)).age(i).build();
            Thread.sleep(3000);
            log.info("random tid:{} pid:{} index:{} dog:{}", Thread.currentThread().getId(), ProcessHandle.current().pid(), i, dog.toString());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}