package com.k4ln.debug4j.socket;

import cn.hutool.extra.spring.SpringUtil;
import com.k4ln.debug4j.protocol.jwdp.TFProtocolDecoder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.extension.processor.AbstractMessageProcessor;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;
import org.smartboot.socket.transport.WriteBuffer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 透明转发客户端
 */
@Slf4j
@Component
public class SocketTFProxyClient {

    @Getter
    AioQuickClient client;

    @Getter
    AbstractMessageProcessor<byte[]> processor;

    /**
     * clientId -> client
     */
    final Map<String, AioSession> sessionMap = new HashMap<>();

    /**
     * 连接客户端
     * @throws Exception
     */
    private AioSession connect(String callbackSessionId, String host, Integer port) throws Exception {

        processor = new AbstractMessageProcessor<>(){

            // Target -> Client
            @Override
            public void process0(AioSession session, byte[] body) {
                //  Client -> Server
                SocketTFProxyServer jdwpServer = SpringUtil.getBean("socketTFProxyServer");
                jdwpServer.sendMessage(session.getAttachment(), body);
            }

            @Override
            public void stateEvent0(AioSession session, StateMachineEnum stateMachineEnum, Throwable throwable) {
                if (stateMachineEnum.equals(StateMachineEnum.NEW_SESSION)) {
                    sessionMap.put(session.getSessionID(), session);
                    session.setAttachment(callbackSessionId);
                    log.info("TFProxy client session:{} connected", session.getSessionID());
                } else if (stateMachineEnum.equals(StateMachineEnum.SESSION_CLOSED)) {
                    log.info("TFProxy client session:{} disConnected", session.getSessionID());
                } else if (throwable != null) {
                    throwable.printStackTrace();
                }
            }
        };

        client = new AioQuickClient(host, port, new TFProtocolDecoder(), processor);
        client.setWriteBuffer(1024, 1024);
        client.setReadBufferSize(1024 * 1024);
        return client.start();
    }

    /**
     * 开启客户端
     * @param sessionId
     * @param callbackSessionId
     * @param host
     * @param port
     * @return
     * @throws Exception
     */
    public AioSession start(String sessionId, String callbackSessionId, String host, Integer port) throws Exception {
        if (sessionId != null){
            AioSession session = sessionMap.get(sessionId);
            if (session != null && !session.isInvalid()){
                return session;
            }
        }
        return connect(callbackSessionId, host, port);
    }

    /**
     * 发送消息
     * @param sessionId
     * @param body
     * @param callbackSessionId
     * @param host
     * @param port
     * @return
     */
    public String sendMessage(String sessionId, byte[] body, String callbackSessionId, String host, Integer port) {
        try {
            AioSession session = start(sessionId, callbackSessionId, host, port);
            WriteBuffer writeBuffer = session.writeBuffer();
            writeBuffer.writeAndFlush(body);
            return session.getSessionID();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
