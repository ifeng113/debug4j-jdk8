package com.k4ln.debug4j.common.protocol.command.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommandTaskRespMessage {

    /**
     * 请求ID
     */
    private String reqId;

    /**
     * 任务列表
     */
    private List<CommandTaskRespMessage> commandTaskRespMessages;

}
