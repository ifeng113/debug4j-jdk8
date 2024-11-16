package com.k4ln.debug4j.daemon;

import cn.hutool.core.util.RuntimeUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class Debug4jDaemonThread implements Runnable {

    private Debug4jArgs debug4jArgs;

    @Getter
    private Process process;

    public Debug4jDaemonThread(Debug4jArgs debug4jArgs) {
        this.debug4jArgs = debug4jArgs;
    }

    @Override
    public void run() {
        log.info("Daemon thread start by:{}", debug4jArgs.getThreadName());

        // fixme 跳过packing，直接读取本地的文件
        String debug4jHome = "E:\\JavaSpace\\ksiu\\debug4j\\debug4j-boot\\build\\libs";
        File debug4jBootJarFile = new File(debug4jHome, "debug4j-boot-1.0-SNAPSHOT-all.jar");
        if (!debug4jBootJarFile.exists()) {
            throw new IllegalStateException("can not find debug4j-boot-1.0-SNAPSHOT-all.jar under debug4jHome: " + debug4jHome);
        }

        process = RuntimeUtil.exec("java", "-jar", debug4jBootJarFile.getAbsolutePath(), debug4jArgs.toString());
        log.info("Debug4j Boot start with pid:{}", process.pid());
    }
}
