package com.k4ln.debug4j.common.process;

import oshi.SystemInfo;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.Collections;
import java.util.List;

public class ProcessHandle {

    public static OperatingSystem operatingSystem;

    static {
        SystemInfo systemInfo = new SystemInfo();
        operatingSystem = systemInfo.getOperatingSystem();
    }

    /**
     * 获取当前进程PID
     * @return
     */
    public static long pid() {
        return operatingSystem.getProcessId();
    }

    /**
     * 检查某个进程是否存活
     * @param pid
     * @return
     */
    public static boolean isAlive(long pid) {
        List<OSProcess> processes = operatingSystem.getProcesses(Collections.singletonList(Math.toIntExact(pid)));
        return processes != null && !processes.isEmpty();
    }
}
