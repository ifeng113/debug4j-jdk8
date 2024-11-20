package com.k4ln.debug4j;

import java.io.PrintStream;

public class LogCaptureAgent {

    // 日志重定向
    private static void redirectSystemStreams() {

        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        PrintStream newOut = new PrintStream(System.out) {
            @Override
            public void println(String x) {
                originalOut.println("[重定向日志] " + x);
                super.println(x); // 保持原有输出
            }
        };

        // 重定向标准输出和错误输出
        System.setOut(newOut);
        System.setErr(newOut);

        System.out.println("[Agent] 日志重定向成功！");
    }

}
