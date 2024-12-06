package com.k4ln.debug4j.core.proxy;

import com.k4ln.debug4j.common.protocol.socket.TFProtocolDecoder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.smartboot.socket.StateMachineEnum;
import org.smartboot.socket.extension.processor.AbstractMessageProcessor;
import org.smartboot.socket.transport.AioQuickClient;
import org.smartboot.socket.transport.AioSession;
import org.smartboot.socket.transport.WriteBuffer;

import static com.k4ln.debug4j.core.client.SocketClient.callbackMessage;
import static com.k4ln.debug4j.core.client.SocketClient.clientClose;

/**
 * 透明转发客户端
 */
@Slf4j
public class SocketTFProxyClient {

    @Getter
    AioSession session;

    @Getter
    AbstractMessageProcessor<byte[]> processor;

    /**
     * 连接客户端
     *
     * @throws Exception
     */
    public void start(Integer clientId, String host, Integer port) throws Exception {

        processor = new AbstractMessageProcessor<byte[]>() {

            // Target -> Client
            @Override
            public void process0(AioSession session, byte[] body) {
                //  Client -> Server
                callbackMessage(clientId, body);
            }

            @Override
            public void stateEvent0(AioSession session, StateMachineEnum stateMachineEnum, Throwable throwable) {
                if (stateMachineEnum.equals(StateMachineEnum.NEW_SESSION)) {
                    log.info("TFProxy client session:{} connected", session.getSessionID());
                } else if (stateMachineEnum.equals(StateMachineEnum.SESSION_CLOSED)) {
                    clientClose(clientId);
                    log.info("TFProxy client session:{} disConnected", session.getSessionID());
                } else if (throwable != null) {
                    throwable.printStackTrace();
                }
            }
        };

        AioQuickClient client = new AioQuickClient(host, port, new TFProtocolDecoder(), processor);
        session = client.start();
    }

    /**
     * 发送消息
     *
     * @param body
     */
    public void sendMessage(byte[] body) {
        try {
            WriteBuffer writeBuffer = session.writeBuffer();
            writeBuffer.write(body);
            writeBuffer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
