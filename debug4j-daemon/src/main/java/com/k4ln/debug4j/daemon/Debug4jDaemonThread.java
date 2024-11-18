package com.k4ln.debug4j.daemon;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.ZipUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Slf4j
public class Debug4jDaemonThread implements Runnable {

    private static final int TEMP_DIR_ATTEMPTS = 10000;

    private Debug4jArgs debug4jArgs;

    @Getter
    private Process process;

    public Debug4jDaemonThread(Debug4jArgs debug4jArgs) {
        this.debug4jArgs = debug4jArgs;
    }

    @Override
    public void run() {
        log.info("Daemon thread start pid:{} by:{}", ProcessHandle.current().pid(), debug4jArgs.getThreadName());
        URL bootUrl = this.getClass().getClassLoader().getResource("debug4j-boot.zip");
        if (bootUrl != null) {
            try {
                File tempDebug4jDir = createTempDir();
                ZipUtil.unzip(bootUrl.openStream(), tempDebug4jDir, CharsetUtil.CHARSET_UTF_8);
                File debug4jBootJarFile = new File(tempDebug4jDir, "debug4j-boot.jar");
                if (!debug4jBootJarFile.exists()) {
                    throw new IllegalStateException("can not find debug4j-boot.jar under tempDebug4jDir: " + tempDebug4jDir);
                }
                process = RuntimeUtil.exec("java", "-jar", debug4jBootJarFile.getAbsolutePath(), debug4jArgs.toString());
                log.info("Debug4j Boot start with pid:{}", process.pid());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("can not getResources debug4j-boot.zip from classloader: "
                    + this.getClass().getClassLoader());
        }
    }

    private static File createTempDir() {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        String baseName = "debug4j-" + System.currentTimeMillis() + "-";
        for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
            File tempDir = new File(baseDir, baseName + counter);
            if (tempDir.mkdir()) {
                return tempDir;
            }
        }
        throw new IllegalStateException("Failed to create directory within " + TEMP_DIR_ATTEMPTS + " attempts (tried "
                + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
    }
}
