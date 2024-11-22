package com.k4ln.debug4j.config;

import com.k4ln.debug4j.service.AttachHub;
import com.k4ln.debug4j.socket.SocketServer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SocketServerProperties.class)
public class SocketServerConfiguration {

    @Bean
    public SocketServer socketServer(SocketServerProperties serverProperties, AttachHub attachHub) {
        return new SocketServer(serverProperties, attachHub);
    }
}
