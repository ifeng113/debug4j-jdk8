package com.k4ln.debug4j.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class PortScannerUtil {

    /**
     * 获取可用端口
     * @param minPort
     * @param maxPort
     * @return
     */
    public static Integer getAvailablePort(int minPort, int maxPort) {
        Set<Integer> usedPorts = getUsedPortsBySystemCommand();
        for (int port = minPort; port <= maxPort; port++) {
            if (!usedPorts.contains(port)) {
                return port;
            }
        }
        return null;
    }

    /**
     * 获取操作系统中已使用的端口（使用系统命令）。
     *
     * @return 已用端口的集合
     */
    public static Set<Integer> getUsedPortsBySystemCommand() {
        Set<Integer> usedPorts = new HashSet<>();
        String command;

        // 根据操作系统选择命令
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            command = "netstat -aon";
        } else {
            command = "netstat -tuln";
        }

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.trim().split("\\s+");
                if (tokens.length > 3) {
                    String addressPort = tokens[3]; // 获取本地地址端口信息
                    int colonIndex = addressPort.lastIndexOf(':');

                    if (colonIndex != -1) {
                        try {
                            int port = Integer.parseInt(addressPort.substring(colonIndex + 1));
                            usedPorts.add(port);
                        } catch (NumberFormatException e) {
                            // 忽略无法解析的端口
                        }
                    }
                }
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usedPorts;
    }

}
