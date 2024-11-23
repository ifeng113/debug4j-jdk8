package com.k4ln.debug4j.core.attach.task;

import cn.hutool.core.io.file.Tailer;
import cn.hutool.core.io.watch.WatchMonitor;
import com.alibaba.fastjson2.JSON;
import com.k4ln.debug4j.common.protocol.command.Command;
import com.k4ln.debug4j.common.protocol.command.CommandTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskInfo {

    /**
     * 请求ID
     */
    private String reqId;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 监听超时（分钟）
     */
    private Integer expire;

    /**
     * 文件监听器
     */
    private Tailer tailer;
}
