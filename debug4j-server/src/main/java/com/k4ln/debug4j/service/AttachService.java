package com.k4ln.debug4j.service;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.HashUtil;
import com.k4ln.debug4j.common.daemon.Debug4jMode;
import com.k4ln.debug4j.common.protocol.command.message.CommandAttachReqMessage;
import com.k4ln.debug4j.common.protocol.command.message.CommandAttachRespMessage;
import com.k4ln.debug4j.common.protocol.command.message.CommandInfoMessage;
import com.k4ln.debug4j.common.protocol.socket.ProtocolTypeEnum;
import com.k4ln.debug4j.common.response.exception.abort.BusinessAbort;
import com.k4ln.debug4j.controller.vo.AttachClassAllReqVO;
import com.k4ln.debug4j.controller.vo.AttachClassSourceReqVO;
import com.k4ln.debug4j.socket.SocketServer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AttachService {

    @Resource
    AttachHub attachHub;

    @Resource
    SocketServer socketServer;

    /**
     * 客户端session检查
     * @param clientSessionId
     */
    private void clientSessionCheck(String clientSessionId) {
        if (!socketServer.getSessionMap().containsKey(clientSessionId)) {
            throw new BusinessAbort("not found remote client");
        } else {
            CommandInfoMessage commandInfoMessage = socketServer.getInfoMessageMap().get(clientSessionId);
            if (commandInfoMessage == null || commandInfoMessage.getDebug4jMode().equals(Debug4jMode.process)){
                throw new BusinessAbort("not found attach client");
            }
        }
    }

    /**
     * 获取所有类
     * @param attachClassAllReqVO
     * @return
     */
    public List<String> getClassNames(AttachClassAllReqVO attachClassAllReqVO) {
        clientSessionCheck(attachClassAllReqVO.getClientSessionId());
        String reqId = UUID.fastUUID().toString(true);
        CommandAttachRespMessage attachResp = attachHub.syncResult(reqId, () ->
                socketServer.sendMessage(attachClassAllReqVO.getClientSessionId(), HashUtil.fnvHash(reqId), ProtocolTypeEnum.COMMAND,
                        CommandAttachReqMessage.buildClassAllMessage(reqId, attachClassAllReqVO.getPackageName())),
                CommandAttachRespMessage.class);
        if (attachResp != null) {
            return attachResp.getClassNames();
        }
        return new ArrayList<>();
    }

    /**
     * 获取类源码
     * @param attachClassSourceReqVO
     * @return
     */
    public String getClassSource(AttachClassSourceReqVO attachClassSourceReqVO) {
        clientSessionCheck(attachClassSourceReqVO.getClientSessionId());
        String reqId = UUID.fastUUID().toString(true);
        CommandAttachRespMessage attachResp = attachHub.syncResult(reqId, () ->
                        socketServer.sendMessage(attachClassSourceReqVO.getClientSessionId(), HashUtil.fnvHash(reqId), ProtocolTypeEnum.COMMAND,
                                CommandAttachReqMessage.buildClassSourceMessage(reqId, attachClassSourceReqVO.getClassName())),
                CommandAttachRespMessage.class);
        if (attachResp != null) {
            return attachResp.getSourceCode();
        }
        return null;
    }
}
