package com.k4ln.demo;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class AgentMain {

    @SneakyThrows
    public static void main(String[] args) {
        List<VirtualMachineDescriptor> vms = VirtualMachine.list();
        for (VirtualMachineDescriptor vm : vms) {
            log.info(String.format("thread=%s, id=%s, displayName=%s", Thread.currentThread().getId(), vm.id(), vm.displayName()));
            if ("com.k4ln.demo.Demo1Main".equals(vm.displayName())) {
                VirtualMachine machine = VirtualMachine.attach(vm.id());
                machine.loadAgent("E:\\JavaSpace\\ksiu\\debug4j\\debug4j-agent\\build\\libs\\debug4j-agent-1.0-SNAPSHOT-all.jar");
            }
        }
    }
}
