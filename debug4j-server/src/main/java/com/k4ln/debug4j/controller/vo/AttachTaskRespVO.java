package com.k4ln.debug4j.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachTaskRespVO {

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 监听超时（分钟）
     */
    private Integer expire;

}
