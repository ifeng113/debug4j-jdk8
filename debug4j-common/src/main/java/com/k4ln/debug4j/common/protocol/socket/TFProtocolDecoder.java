package com.k4ln.debug4j.common.protocol.socket;

import lombok.extern.slf4j.Slf4j;
import org.smartboot.socket.Protocol;

import java.nio.ByteBuffer;

/**
 * 透明转发
 */
@Slf4j
public class TFProtocolDecoder implements Protocol<byte[]> {

    @Override
    public byte[] decode(ByteBuffer readBuffer, org.smartboot.socket.transport.AioSession session) {
        byte[] body = new byte[readBuffer.remaining()];
        readBuffer.get(body);
        readBuffer.mark();
        return body;
    }
}
