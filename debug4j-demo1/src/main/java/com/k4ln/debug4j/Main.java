package com.k4ln.debug4j;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            logNumber(i);
        }
    }

    private static void logNumber(int i) {
        try {
            Thread.sleep(3000);
            log.info("random number:{} index:{}", RandomUtil.randomNumbers(10), i);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}