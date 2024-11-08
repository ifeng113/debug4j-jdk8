package com.k4ln.debug4j;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class Debug4jServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(Debug4jServerApplication.class, args);
    }
}