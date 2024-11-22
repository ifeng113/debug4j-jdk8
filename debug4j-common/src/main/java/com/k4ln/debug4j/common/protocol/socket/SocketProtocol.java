package com.k4ln.debug4j.common.protocol.socket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocketProtocol {

    /**
     * 协议版本
     */
    @Builder.Default
    private Integer version = 1;

    /**
     * 协议类型
     */
    private ProtocolTypeEnum protocolType;

    /**
     * 是否分包
     */
    private Boolean subcontract;

    /**
     * 分包总数
     */
    @Builder.Default
    private Integer subcontractCount = 1;

    /**
     * 分包序号
     */
    @Builder.Default
    private Integer subcontractIndex = 1;

    /**
     * 客户端ID
     */
    private Integer clientId;

    /**
     * 数据内容
     */
    private byte[] body;

}
