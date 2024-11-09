package com.k4ln.debug4j.config;

import com.k4ln.debug4j.socket.SocketServer;
import com.k4ln.debug4j.socket.SocketTFProxyServer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ServerRunner implements ApplicationRunner {

    @Resource
    SocketServer server;

    @Resource
    SocketTFProxyServer tfProxyServer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        server.start();
        tfProxyServer.start();
    }
}
