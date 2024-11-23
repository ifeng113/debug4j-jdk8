package com.k4ln.debug4j.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.HashUtil;
import com.k4ln.debug4j.common.daemon.Debug4jMode;
import com.k4ln.debug4j.common.protocol.command.message.CommandAttachReqMessage;
import com.k4ln.debug4j.common.protocol.command.message.CommandAttachRespMessage;
import com.k4ln.debug4j.common.protocol.command.message.CommandInfoMessage;
import com.k4ln.debug4j.common.protocol.command.message.CommandTaskRespMessage;
import com.k4ln.debug4j.common.protocol.command.message.CommandTaskReqMessage;
import com.k4ln.debug4j.common.protocol.socket.ProtocolTypeEnum;
import com.k4ln.debug4j.common.response.exception.abort.BusinessAbort;
import com.k4ln.debug4j.controller.vo.*;
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
     *
     * @param clientSessionId
     */
    private void clientSessionCheck(String clientSessionId) {
        if (!socketServer.getSessionMap().containsKey(clientSessionId)) {
            throw new BusinessAbort("not found remote client");
        } else {
            CommandInfoMessage commandInfoMessage = socketServer.getInfoMessageMap().get(clientSessionId);
            if (commandInfoMessage == null || commandInfoMessage.getDebug4jMode().equals(Debug4jMode.process)) {
                throw new BusinessAbort("not found attach client");
            }
        }
    }

    /**
     * 获取所有类
     *
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
     *
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
        return "";
    }

    /**
     * 获取任务列表
     *
     * @param attachTaskReqVO
     * @return
     */
    public List<AttachTaskRespVO> getTask(AttachTaskReqVO attachTaskReqVO) {
        clientSessionCheck(attachTaskReqVO.getClientSessionId());
        String reqId = UUID.fastUUID().toString(true);
        CommandTaskRespMessage attachResp = attachHub.syncResult(reqId, () ->
                        socketServer.sendMessage(attachTaskReqVO.getClientSessionId(), HashUtil.fnvHash(reqId), ProtocolTypeEnum.COMMAND,
                                CommandTaskReqMessage.buildTaskAllMessage(reqId)),
                CommandTaskRespMessage.class);
        if (attachResp != null) {
            return BeanUtil.copyToList(attachResp.getCommandTaskReqMessages(), AttachTaskRespVO.class);
        }
        return new ArrayList<>();
    }

    /**
     * 开启任务
     *
     * @param openReqVO
     * @return
     */
    public List<AttachTaskRespVO> openTask(AttachTaskOpenReqVO openReqVO) {
        clientSessionCheck(openReqVO.getClientSessionId());
        String reqId = UUID.fastUUID().toString(true);
        CommandTaskRespMessage attachResp = attachHub.syncResult(reqId, () ->
                        socketServer.sendMessage(openReqVO.getClientSessionId(), HashUtil.fnvHash(reqId), ProtocolTypeEnum.COMMAND,
                                CommandTaskReqMessage.buildTaskOpenMessage(reqId, openReqVO.getFilePath(), openReqVO.getExpire())),
                CommandTaskRespMessage.class);
        if (attachResp != null) {
            return BeanUtil.copyToList(attachResp.getCommandTaskReqMessages(), AttachTaskRespVO.class);
        }
        return new ArrayList<>();
    }

    /**
     * 关闭任务
     *
     * @param closeReqVO
     * @return
     */
    public List<AttachTaskRespVO> closeTask(AttachTaskCloseReqVO closeReqVO) {
        clientSessionCheck(closeReqVO.getClientSessionId());
        String reqId = UUID.fastUUID().toString(true);
        CommandTaskRespMessage attachResp = attachHub.syncResult(reqId, () ->
                        socketServer.sendMessage(closeReqVO.getClientSessionId(), HashUtil.fnvHash(reqId), ProtocolTypeEnum.COMMAND,
                                CommandTaskReqMessage.buildTaskCloseMessage(reqId, closeReqVO.getFilePath())),
                CommandTaskRespMessage.class);
        if (attachResp != null) {
            return BeanUtil.copyToList(attachResp.getCommandTaskReqMessages(), AttachTaskRespVO.class);
        }
        return new ArrayList<>();
    }

    /**
     * 获取任务详情
     *
     * @param path
     * @return
     */
    public List<String> getTaskDetails(String path) {
        return null;
    }
}
