package com.k4ln.debug4j.core;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.k4ln.debug4j.core.client.SocketClient;
import com.k4ln.debug4j.protocol.command.message.CommandInfoMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Debugger {

    private static SocketClient socketClient;

    private static CommandInfoMessage commandInfoMessage;

    private static ScheduledThreadPoolExecutor scheduledExecutor;

    public static void start(String application, String host, Integer port, String key, Long pid, Integer jdwpPort) {
        start(application, UUID.fastUUID().toString(true), host, port, key, pid, jdwpPort);
    }

    public static void start(String application, String uniqueId, String host, Integer port, String key, Long pid, Integer jdwpPort) {
        commandInfoMessage = CommandInfoMessage.builder()
                .applicationName(application)
                .socketClientHost(NetUtil.getLocalHostName())
                .socketClientIp(NetUtil.getLocalhostStr())
                .uniqueId(uniqueId == null ? UUID.fastUUID().toString(true) : uniqueId)
                .pid(pid)
                .jdwpPort(jdwpPort)
                .build();
        scheduledExecutor = ThreadUtil.createScheduledExecutor(10);
        scheduledExecutor.scheduleWithFixedDelay(buildKeepAliveRunnable(host, port, key), 0, 10, TimeUnit.SECONDS);
    }

    public static void shutdown() {
        scheduledExecutor.shutdown();
        if (socketClient != null){
            socketClient.shutdown();
        }
    }

    /**
     * 构建检查线程
     *
     * @return
     */
    private static Runnable buildKeepAliveRunnable(String host, Integer port, String key) {
        return () -> {
            try {
                if (socketClient == null || !socketClient.isAlive()) {
                    socketClient = new SocketClient(key, host, port, commandInfoMessage);
                    socketClient.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
