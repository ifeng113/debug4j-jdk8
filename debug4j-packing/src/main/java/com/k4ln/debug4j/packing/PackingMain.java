package com.k4ln.debug4j.packing;

import cn.hutool.core.util.ZipUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class PackingMain {

    public static void main(String[] args) {
        String bootPath = System.getProperty("user.dir").replace("\\debug4j-packing", "") +
                "\\debug4j-boot\\build\\libs\\debug4j-boot.jar";
        File bootFile = new File(bootPath);
        if (bootFile.exists()) {
            String zipPath = System.getProperty("user.dir").replace("\\debug4j-packing", "") +
                    "\\debug4j-packing\\src\\main\\resources\\debug4j-boot.zip";
            ZipUtil.zip(new File(zipPath), false, bootFile);
            log.warn("file zip success in:{}", zipPath);
        } else {
            log.warn("file does not exist. bootPath:{}", bootPath);
        }
    }
}
