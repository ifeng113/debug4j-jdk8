package com.k4ln.debug4j.socket;

import cn.hutool.extra.spring.SpringUtil;
import com.k4ln.debug4j.protocol.SocketProtocolUtil;
import com.k4ln.debug4j.protocol.jwdp.TFProtocolDecoder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.extension.processor.AbstractMessageProcessor;
import org.smartboot.socket.transport.AioQuickServer;
import org.smartboot.socket.transport.AioSession;
import org.smartboot.socket.transport.WriteBuffer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SocketTFProxyServer {

    @Value("${ss.tfp-server-port}")
    Integer tfpServerPort;

    @Value("${ss.client.tfp-host}")
    String tfpClientHost;

    @Value("${ss.client.tfp-port}")
    Integer tfpClientPort;

    @Getter
    AioQuickServer server;

    @Getter
    AbstractMessageProcessor<byte[]> processor;

    /**
     * serverId -> server
     */
    final Map<String, AioSession> sessionMap = new HashMap<>();

    public void start() throws Exception {

        processor = new AbstractMessageProcessor<>() {

            // Source -> Server
            @Override
            public void process0(AioSession session, byte[] body) {
                //  Server -> Client
                SocketTFProxyClient jdwpClient = SpringUtil.getBean("socketTFProxyClient");
                String clientSessionId = jdwpClient.sendMessage(session.getAttachment(), body,
                        session.getSessionID(), tfpClientHost, tfpClientPort);
                session.setAttachment(clientSessionId);
            }

            @Override
            public void stateEvent0(AioSession session, StateMachineEnum stateMachineEnum, Throwable throwable) {
                if (stateMachineEnum.equals(StateMachineEnum.NEW_SESSION)) {
                    try {
                        log.info("TFProxy server sessionId:{} ip:{} port:{} time:{}", session.getSessionID(),
                                session.getRemoteAddress().getHostName(), session.getRemoteAddress().getPort(),
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sessionMap.put(session.getSessionID(), session);
                    log.info("TFProxy server session:{} connected", session.getSessionID());
                } else if (stateMachineEnum.equals(StateMachineEnum.SESSION_CLOSED)) {
                    sessionMap.remove(session.getSessionID());
                    log.info("TFProxy server session:{} disConnected", session.getSessionID());
                } else if (throwable != null) {
                    throwable.printStackTrace();
                }
            }
        };

        server = new AioQuickServer(tfpServerPort, new TFProtocolDecoder(), processor);
        server.setReadBufferSize(SocketProtocolUtil.READ_BUFFER_SIZE * SocketProtocolUtil.READ_BUFFER_SIZE);
        server.start();

        log.info("TFProxy server started at port {}", tfpServerPort);
    }

    public void sendMessage(String sessionId, byte[] body) {
        try {
            AioSession session = sessionMap.get(sessionId);
            WriteBuffer writeBuffer = session.writeBuffer();
            writeBuffer.writeAndFlush(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
