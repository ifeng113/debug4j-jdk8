package com.k4ln.debug4j;

import cn.hutool.core.util.RandomUtil;
import com.sun.tools.attach.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class Demo1Main {

    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        // https://blog.csdn.net/2301_80520893/article/details/136390397 [VM Options:-Djdk.attach.allowAttachSelf=true]
        List<VirtualMachineDescriptor> vms = VirtualMachine.list();
        for (VirtualMachineDescriptor vm : vms) {
            log.info(String.format("thread=%s, id=%s, displayName=%s", Thread.currentThread().getId(), vm.id(), vm.displayName()));
            if (".\\debug4j-demo1-1.0-SNAPSHOT-all.jar".equals(vm.displayName())) {
                VirtualMachine machine = VirtualMachine.attach(vm.id());
                machine.loadAgent("E:\\JavaSpace\\ksiu\\debug4j\\debug4j-agent\\build\\libs\\debug4j-agent-1.0-SNAPSHOT-all.jar");
            }
        }
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