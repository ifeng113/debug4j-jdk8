package com.k4ln.debug4j.socket;

import cn.hutool.core.util.ArrayUtil;
import com.k4ln.debug4j.protocol.ProtocolTypeEnum;
import com.k4ln.debug4j.protocol.SocketProtocol;
import com.k4ln.debug4j.protocol.SocketProtocolDecoder;
import com.k4ln.debug4j.protocol.SocketProtocolUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.extension.plugins.HeartPlugin;
import org.smartboot.socket.extension.processor.AbstractMessageProcessor;
import org.smartboot.socket.transport.AioQuickServer;
import org.smartboot.socket.transport.AioSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class SocketServer {

    @Value("${ss.server-port}")
    Integer serverPort;

    @Value("${ss.key}")
    String key;

    @Getter
    AioQuickServer server;

    @Getter
    AbstractMessageProcessor<SocketProtocol> processor;

    final Map<String, AioSession> sessionMap = new HashMap<>();

    final Map<String, Integer> authSessionMap = new HashMap<>();

    final Map<String, byte[]> sessionPackaging = new HashMap<>();

    public void start() throws Exception {

        processor = new AbstractMessageProcessor<>() {

            @Override
            public void process0(AioSession session, SocketProtocol protocol) {
                // 鉴权
                if (!checkAuth(session.getSessionID())) {
                    return;
                }
                if (protocol.getSubcontract()) {
                    sessionPackaging.put(getSessionPackagingKey(session, protocol.getProtocolType()),
                            ArrayUtil.addAll(sessionPackaging.get(session.getSessionID()), protocol.getBody()));
                    if (protocol.getSubcontractCount().equals(protocol.getSubcontractIndex())) {
                        messageHandler(session, protocol.getProtocolType(),
                                sessionPackaging.get(getSessionPackagingKey(session, protocol.getProtocolType())));
                        sessionPackaging.remove(getSessionPackagingKey(session, protocol.getProtocolType()));
                    } else {
                        sessionPackaging.put(getSessionPackagingKey(session, protocol.getProtocolType()), protocol.getBody());
                    }
                } else {
                    messageHandler(session, protocol.getProtocolType(), protocol.getBody());
                }
            }

            private Boolean checkAuth(String sessionID) {
                Integer heartTimes = authSessionMap.get(sessionID);
                return heartTimes != null && heartTimes == -1;
            }

            private void messageHandler(AioSession session, ProtocolTypeEnum protocolType, byte[] data) {
                switch (protocolType) {
                    case TEXT -> log.info(new String(data));
                    case AUTH -> {
                        if (key.equals(new String(data))) {
                            authSessionMap.put(session.getSessionID(), -1);
                        }
                    }
                    case PROXY -> {
                        // TODO
                    }
                }
            }

            private static String getSessionPackagingKey(AioSession session, ProtocolTypeEnum proxyProtocol) {
                return session.getSessionID() + "-" + proxyProtocol.name();
            }

            @Override
            public void stateEvent0(AioSession session, StateMachineEnum stateMachineEnum, Throwable throwable) {
                if (stateMachineEnum.equals(StateMachineEnum.NEW_SESSION)) {
                    sessionMap.put(session.getSessionID(), session);
                    authSessionMap.put(session.getSessionID(), 0);
                    log.info("session:{} connected", session.getSessionID());
                } else if (stateMachineEnum.equals(StateMachineEnum.SESSION_CLOSED)) {
                    sessionMap.remove(session.getSessionID());
                    authSessionMap.remove(session.getSessionID());
                    log.info("session:{} disConnected", session.getSessionID());
                } else if (throwable != null) {
                    throwable.printStackTrace();
                }
            }
        };

        processor.addPlugin(new HeartPlugin<>(5, TimeUnit.SECONDS) {

            @Override
            public void sendHeartRequest(AioSession session) {
                Integer heartTimes = authSessionMap.get(session.getSessionID());
                if (heartTimes == null) {
                    session.close();
                    return;
                } else {
                    if (heartTimes > 3) {
//                        session.close();
//                        return;
                    } else if (heartTimes != -1) {
                        authSessionMap.put(session.getSessionID(), heartTimes + 1);
                    }
                }
                sendMessage(session.getSessionID(), ProtocolTypeEnum.HEART, null);
            }

            @Override
            public boolean isHeartMessage(AioSession session, SocketProtocol socketProtocol) {
                return socketProtocol.getProtocolType().equals(ProtocolTypeEnum.HEART);
            }
        });

        server = new AioQuickServer(serverPort, new SocketProtocolDecoder(), processor);
        server.setReadBufferSize(SocketProtocolUtil.READ_BUFFER_SIZE);
        server.start();

        log.info("socket server started at port {}", serverPort);
    }


    /**
     * 根据sessionId发送消息
     *
     * @param sessionId
     * @param protocolType
     * @param body
     */
    public void sendMessage(String sessionId, ProtocolTypeEnum protocolType, byte[] body) {
        AioSession session = sessionMap.get(sessionId);
        if (session != null) {
            SocketProtocolUtil.sendMessage(session, protocolType, body);
        }
    }

}
