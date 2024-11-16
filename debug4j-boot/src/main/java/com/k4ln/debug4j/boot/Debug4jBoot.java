package com.k4ln.debug4j.boot;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Debug4jBoot {

    public static void main(String[] args) {
        log.info("args:{}", JSON.toJSONString(args));
        Long appProcessId = null;
        Integer jdwpPort = null;
        String arg = args[0];
        if (StrUtil.isNotBlank(arg)){
            String[] parameters = arg.split(",");
            for (String parameter : parameters){
                String[] entry = parameter.split("=");
                if (entry.length == 2){
                    if (entry[0].equals("pid")){
                        appProcessId = Long.parseLong(entry[1]);
                    } else if (entry[0].equals("jdwp") && !"null".equals(entry[1])){
                        jdwpPort = Integer.parseInt(entry[1]);
                    }
                }
            }

            checkAppProcess(appProcessId, jdwpPort);
        } else {
            log.error("Debug4j Boot shutdown with empty args");
        }
    }

    private static void checkAppProcess(Long appProcessId, Integer jdwpPort) {
        if (appProcessId == null){
            log.error("Debug4j Boot shutdown with empty pid");
            return;
        }

        // fixme Debugger.start(); +  jdwpPort

        boolean bootRun = true;
        while (bootRun){
            Boolean isAlive = ProcessHandle.of(appProcessId)
                    .map(ProcessHandle::isAlive)
                    .orElse(false);
            log.info("checkAppProcess pid:{} isAlive:{}", appProcessId, isAlive);
            if (!isAlive){
                bootRun = false;
            } else {
                try {
                    Thread.sleep(10000);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        log.info("checkAppProcess break. Debug4j Boot shutdown");
    }


}
