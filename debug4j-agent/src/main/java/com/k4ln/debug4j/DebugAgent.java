package com.k4ln.debug4j;

import com.sun.tools.attach.VirtualMachine;
import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.Instrumentation;

@Slf4j
public class DebugAgent {

    // 执行两次：https://blog.csdn.net/NEWCIH/article/details/129185402
    public static void premain(String agentArgs, Instrumentation inst) {
        log.info("agent premain run with agentArgs:{} inst:{}", agentArgs, inst);
//        enableRemoteDebug();
    }

    private static void enableRemoteDebug() {
        try {
            // com.sun.tools.attach.AgentLoadException: Failed to load agent library: _Agent_OnAttach@12 is not available in jdwp
            // 无法通过 Attach API 动态加载 jdwp
            log.info("pid:{}", ProcessHandle.current().pid());
            VirtualMachine vm = VirtualMachine.attach(String.valueOf(ProcessHandle.current().pid()));
            vm.startLocalManagementAgent();
            vm.loadAgentLibrary("jdwp", "transport=dt_socket,server=y,suspend=n,address=*:5005");
            vm.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}