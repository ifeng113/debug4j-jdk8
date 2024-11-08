package com.k4ln.debug4j.protocol;

import lombok.extern.slf4j.Slf4j;
import org.smartboot.socket.Protocol;

import java.nio.ByteBuffer;

@Slf4j
public class SocketProtocolDecoder implements Protocol<SocketProtocol> {

    @Override
    public SocketProtocol decode(ByteBuffer readBuffer, org.smartboot.socket.transport.AioSession session) {
        return SocketProtocolUtil.analysisProxyProtocol(readBuffer);
    }
}
