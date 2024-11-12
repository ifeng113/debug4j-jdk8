package com.k4ln.debug4j.protocol.command.message;

import cn.hutool.core.net.NetUtil;
import com.alibaba.fastjson2.JSON;
import com.k4ln.debug4j.protocol.command.Command;
import com.k4ln.debug4j.protocol.command.CommandTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandInfoMessage {

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 通信客户端主机名
     */
    private String socketClientHost;

    /**
     * 通信客户端主Ip
     */
    private String socketClientIp;

    /**
     * 唯一ID
     */
    private String uniqueId;

}
