package com.k4ln.debug4j.core.client;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.HashUtil;
import com.alibaba.fastjson2.JSON;
import com.k4ln.debug4j.common.daemon.Debug4jMode;
import com.k4ln.debug4j.common.protocol.command.Command;
import com.k4ln.debug4j.common.protocol.command.CommandTypeEnum;
import com.k4ln.debug4j.common.protocol.command.message.*;
import com.k4ln.debug4j.common.protocol.socket.ProtocolTypeEnum;
import com.k4ln.debug4j.common.protocol.socket.SocketProtocol;
import com.k4ln.debug4j.common.protocol.socket.SocketProtocolDecoder;
import com.k4ln.debug4j.common.utils.SocketProtocolUtil;
import com.k4ln.debug4j.core.Debugger;
import com.k4ln.debug4j.core.attach.Debug4jAttachOperator;
import com.k4ln.debug4j.core.proxy.SocketTFProxyClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.smartboot.socket.MessageProcessor;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.extension.processor.AbstractMessageProcessor;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SocketClient {

    private final String key;

    private final String host;

    private final Integer port;

    private static CommandInfoMessage commandInfoMessage;

    public SocketClient(String key, String host, Integer port, CommandInfoMessage commandInfoMessage) {
        this.key = key;
        this.host = host;
        this.port = port;
        SocketClient.commandInfoMessage = commandInfoMessage;
    }

    @Getter
    static AioSession session;

    /**
     * realClientId -> targetClient
     */
    static final Map<Integer, SocketTFProxyClient> targetClients = new ConcurrentHashMap<>();

    /**
     * socketServer -> packagingData
     */
    final Map<String, byte[]> sessionPackaging = new ConcurrentHashMap<>();

    /**
     * 是否还存活
     *
     * @return
     */
    public boolean isAlive() {
        return session != null && !session.isInvalid();
    }

    public void shutdown() {
        if (session != null) {
            session.close();
        }
    }

    /**
     * 开启socket客户端
     *
     * @throws IOException
     */
    public void start() throws IOException {

        targetClients.clear();

        MessageProcessor<SocketProtocol> processor = new AbstractMessageProcessor<>() {

            @Override
            public void process0(AioSession session, SocketProtocol socketProtocol) {
                switch (socketProtocol.getProtocolType()) {
                    case COMMAND -> {
                        if (socketProtocol.getSubcontract()) {
                            String sessionPackagingKey = getSessionPackagingKey(session, socketProtocol);
                            sessionPackaging.put(sessionPackagingKey, ArrayUtil.addAll(sessionPackaging.get(sessionPackagingKey), socketProtocol.getBody()));
                            if (socketProtocol.getSubcontractCount().equals(socketProtocol.getSubcontractIndex())) {
                                handCommand(socketProtocol, sessionPackaging.get(sessionPackagingKey));
                                sessionPackaging.remove(sessionPackagingKey);
                            }
                        } else {
                            handCommand(socketProtocol, socketProtocol.getBody());
                        }
                    }
                    case PROXY -> {
                        Integer clientId = socketProtocol.getClientId();
                        if (targetClients.containsKey(clientId)) {
                            targetClients.get(clientId).sendMessage(socketProtocol.getBody());
                        } else {
                            log.warn("socketClient proxy no clientId:{}", clientId);
                        }
                    }
                }
            }

            private static String getSessionPackagingKey(AioSession session, SocketProtocol protocol) {
                return session.getSessionID() + "-" + protocol.getProtocolType().name() + "-" + protocol.getClientId();
            }

            private static void handCommand(SocketProtocol socketProtocol, byte[] data) {
                Command command = JSON.parseObject(new String(data), Command.class);
                if (command.getCommand().equals(CommandTypeEnum.LOG)) {
                    log.info(JSON.parseObject(JSON.toJSONString(command.getData()), CommandLogMessage.class).getContent());
                } else if (commandInfoMessage.getDebug4jMode().equals(Debug4jMode.process)) {
                    if (command.getCommand().equals(CommandTypeEnum.PROXY_OPEN)) {
                        CommandProxyMessage proxyMessage = JSON.parseObject(JSON.toJSONString(command.getData()), CommandProxyMessage.class);
                        try {
                            SocketTFProxyClient tfProxyClient = new SocketTFProxyClient();
                            targetClients.put(socketProtocol.getClientId(), tfProxyClient);
                            tfProxyClient.start(socketProtocol.getClientId(), proxyMessage.getHost(), proxyMessage.getPort());
                            log.info("socketClient proxy started successfully clientId:{}", socketProtocol.getClientId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (command.getCommand().equals(CommandTypeEnum.PROXY_CLOSE)) {
                        if (targetClients.containsKey(socketProtocol.getClientId())) {
                            SocketTFProxyClient tfProxyClient = targetClients.get(socketProtocol.getClientId());
                            if (tfProxyClient != null && !tfProxyClient.getSession().isInvalid()) {
                                tfProxyClient.getSession().close();
                            }
                            targetClients.remove(socketProtocol.getClientId());
                            log.info("socketClient proxy closed successfully clientId:{}", socketProtocol.getClientId());
                        } else {
                            log.warn("socketClient command no clientId:{}", socketProtocol.getClientId());
                        }
                    }
                } else if (commandInfoMessage.getDebug4jMode().equals(Debug4jMode.thread)){
                    if (command.getCommand().equals(CommandTypeEnum.ATTACH_REQ_CLASS_ALL)) {
                        CommandAttachReqMessage attachReq = JSON.parseObject(JSON.toJSONString(command.getData()), CommandAttachReqMessage.class);
                        List<String> allClass = Debug4jAttachOperator.getAllClass(Debugger.getInstrumentation(), commandInfoMessage.getPackageName(), attachReq.getPackageName());
                        SocketProtocolUtil.sendMessage(session, HashUtil.fnvHash(attachReq.getReqId()), ProtocolTypeEnum.COMMAND, CommandAttachRespMessage.buildClassAllRespMessage(attachReq.getReqId(), allClass));
                    } else if (command.getCommand().equals(CommandTypeEnum.ATTACH_REQ_CLASS_SOURCE)) {
                        CommandAttachReqMessage attachReq = JSON.parseObject(JSON.toJSONString(command.getData()), CommandAttachReqMessage.class);
                        String classSource = Debug4jAttachOperator.getClassSource(attachReq.getClassName());
                        SocketProtocolUtil.sendMessage(session, HashUtil.fnvHash(attachReq.getReqId()), ProtocolTypeEnum.COMMAND, CommandAttachRespMessage.buildClassSourceRespMessage(attachReq.getReqId(), classSource));
                    }
                }
            }

            @Override
            public void stateEvent0(AioSession session, StateMachineEnum stateMachineEnum, Throwable throwable) {
                if (stateMachineEnum.equals(StateMachineEnum.NEW_SESSION)) {
                    SocketProtocolUtil.sendMessage(session, 0, ProtocolTypeEnum.AUTH, key.getBytes());
                    SocketProtocolUtil.sendMessage(session, 0, ProtocolTypeEnum.COMMAND,
                            JSON.toJSONString(Command.builder().command(CommandTypeEnum.INFO).data(commandInfoMessage).build()).getBytes());
                    log.info("socket client connected");
                } else if (stateMachineEnum.equals(StateMachineEnum.SESSION_CLOSED)) {
                    log.info("socket client disConnected");
                } else if (throwable != null) {
                    throwable.printStackTrace();
                }
            }
        };
        AioQuickClient client = new AioQuickClient(host, port, new SocketProtocolDecoder(), processor);
        client.setReadBufferSize(SocketProtocolUtil.READ_BUFFER_SIZE);
        session = client.start();
    }

    public static void callbackMessage(Integer clientId, byte[] body) {
        SocketProtocolUtil.sendMessage(session, clientId, ProtocolTypeEnum.PROXY, body);
    }

    public static void clientClose(Integer clientId) {
        targetClients.remove(clientId);
        SocketProtocolUtil.sendMessage(session, clientId, ProtocolTypeEnum.COMMAND,
                CommandProxyMessage.buildCommandProxyMessage(CommandTypeEnum.PROXY_CLOSE, null, null));
        log.info("socketClient proxy target closed clientId:{}", clientId);
    }
}
