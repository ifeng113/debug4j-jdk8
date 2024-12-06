package com.k4ln.debug4j.service;

import cn.hutool.core.bean.BeanUtil;
import com.k4ln.debug4j.common.protocol.command.message.CommandInfoMessage;
import com.k4ln.debug4j.controller.vo.ManageClientRespVO;
import com.k4ln.debug4j.socket.SocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ManageService {

    @Resource
    SocketServer socketServer;

    /**
     * 获取客户端
     *
     * @return
     */
    public List<ManageClientRespVO> getClients() {
        Map<String, CommandInfoMessage> infoMessageMap = socketServer.getInfoMessageMap();
        return infoMessageMap.values().stream().map(e -> BeanUtil.toBean(e, ManageClientRespVO.class)).sorted(Comparator.comparing(ManageClientRespVO::getApplicationName)
                .thenComparing(ManageClientRespVO::getUniqueId)
                .thenComparing(ManageClientRespVO::getDebug4jMode).reversed()).collect(Collectors.toList());
    }
}
