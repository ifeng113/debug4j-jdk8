package com.k4ln.debug4j.protocol.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Command<T> {

    /**
     * 指令内容
     */
    private CommandTypeEnum command;

    /**
     * 指令数据
     */
    private T data;

}
