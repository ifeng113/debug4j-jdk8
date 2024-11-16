package com.k4ln.debug4j.daemon;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RuntimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Debug4jDaemon {

    public static final String DEBUG4J_THREAD_NAME = "debug4j-daemon";

    /**
     * 开启守护线程
     */
    public static void start() {
        Debug4jArgs debug4jArgs = loadDebug4jArgs();
        Debug4jDaemonThread debug4jDaemonThread = new Debug4jDaemonThread(debug4jArgs);
        Thread thread = ThreadUtil.newThread(debug4jDaemonThread, DEBUG4J_THREAD_NAME, true);
        thread.start();
        RuntimeUtil.addShutdownHook(() -> {
            Process process = debug4jDaemonThread.getProcess();
            if (process != null) {
                process.destroy();
            }
        });
    }

    /**
     * 装载参数
     * @return
     */
    private static Debug4jArgs loadDebug4jArgs() {
        Debug4jArgs debug4jArgs = new Debug4jArgs();
        debug4jArgs.setPid(ProcessHandle.current().pid());
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> jvmArguments = runtimeMXBean.getInputArguments();
        for (String arg : jvmArguments) {
            if (arg.startsWith("-agentlib:jdwp=transport=dt_socket")) {
                String port = extractPort(arg);
                if (port != null) {
                    try {
                        debug4jArgs.setJdwpPort(Integer.parseInt(port));
                    } catch (Exception e){
                        log.warn("debug4j daemon match jdwp port error arg:{} exception:{}", arg, e.getMessage());
                    }
                }
            }
        }
        debug4jArgs.setThreadName(Thread.currentThread().getName());
        return debug4jArgs;
    }

    /**
     * 提取 JDWP 参数中的端口号
     *
     * @param input 输入的参数字符串
     * @return 提取的端口号，找不到时返回 null
     */
    public static String extractPort(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        // 正则表达式匹配 address 后的端口号
        String regex = "address=[^:]*:(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? matcher.group(1) : null;
    }
}
