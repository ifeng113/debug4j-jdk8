package com.k4ln.debug4j.daemon;

import lombok.*;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Debug4jArgs {

    /**
     * 进程ID
     */
    private Long pid;

    /**
     * 线程名
     */
    private String threadName;

    /**
     * 远程调试端口
     */
    private Integer jdwpPort;

    @Override
    public String toString() {
        return "pid=" + pid +
                ",jdwpPort=" + jdwpPort;
    }
}
