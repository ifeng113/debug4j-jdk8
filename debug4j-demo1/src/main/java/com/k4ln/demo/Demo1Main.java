package com.k4ln.demo;

import cn.hutool.core.util.RandomUtil;
import com.k4ln.debug4j.daemon.Debug4jDaemon;
import com.k4ln.debug4j.daemon.Debug4jMode;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Demo1Main {

    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        // https://blog.csdn.net/2301_80520893/article/details/136390397 [VM Options:-Djdk.attach.allowAttachSelf=true]
//        List<VirtualMachineDescriptor> vms = VirtualMachine.list();
//        for (VirtualMachineDescriptor vm : vms) {
//            log.info(String.format("thread=%s, id=%s, displayName=%s", Thread.currentThread().getId(), vm.id(), vm.displayName()));
//            if (".\\debug4j-demo1-1.0-SNAPSHOT-all.jar".equals(vm.displayName())) {
//                VirtualMachine machine = VirtualMachine.attach(vm.id());
//                machine.loadAgent("E:\\JavaSpace\\ksiu\\debug4j\\debug4j-agent\\build\\libs\\debug4j-agent-1.0-SNAPSHOT-all.jar");
//            }
//        }

//        Debugger.start("demo1", "192.168.1.164", 7988, "k4ln");

        Debug4jDaemon.start("demo1", "192.168.1.164", 7988, "k4ln", Debug4jMode.thread);

        for (int i = 0; i < 1000; i++) {
            logNumber(i);
            if (i == 999) {
                i = 0;
            }
        }
    }

    private static void logNumber(int i) {
        try {
            Dog dog = Dog.builder().name(RandomUtil.randomNumbers(4)).age(i).build();
            Thread.sleep(3000);
//            log.info("random tid:{} pid:{} index:{} dog:{}", Thread.currentThread().getId(), ProcessHandle.current().pid(), i, JSON.toJSONString(dog));
            log.info("random tid:{} pid:{} index:{} dog:{}", Thread.currentThread().getId(), ProcessHandle.current().pid(), i, dog.toString());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

//    public static String toJsonString(Dog dog){
//        return JSON.toJSONString(dog);
//    }
}