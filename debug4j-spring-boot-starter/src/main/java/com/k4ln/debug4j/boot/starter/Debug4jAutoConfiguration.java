package com.k4ln.debug4j.boot.starter;

import cn.hutool.core.util.StrUtil;
import com.k4ln.debug4j.daemon.Debug4jDaemon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

@Slf4j
@ConditionalOnProperty(prefix = "debug4j", value = "enable", matchIfMissing = true)
@EnableConfigurationProperties(Debug4jProperties.class)
public class Debug4jAutoConfiguration {

    public static final String SPRING_APPLICATION_NAME = "spring.application.name";

    final Debug4jProperties debug4jProperties;

    public Debug4jAutoConfiguration(Debug4jProperties debug4jProperties, Environment environment) {
        String applicationName = environment.getProperty(SPRING_APPLICATION_NAME);
        this.debug4jProperties = debug4jProperties;
        if (StrUtil.isBlank(debug4jProperties.getApplication())) {
            debug4jProperties.setApplication(applicationName);
        }
        Debug4jDaemon.start(debug4jProperties.getProxy(), debug4jProperties.getApplication(), debug4jProperties .getPackageName(),
                debug4jProperties.getHost(), debug4jProperties.getPort(), debug4jProperties.getKey());
    }
}
