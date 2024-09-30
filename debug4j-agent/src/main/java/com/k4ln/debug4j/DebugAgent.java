package com.k4ln.debug4j;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.Instrumentation;

@Slf4j
public class DebugAgent {

    public static void premain(String agentArgs, Instrumentation inst) {
        log.info("agent premain run with hutool:{}", RandomUtil.randomNumbers(8));
    }

}