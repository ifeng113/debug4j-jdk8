package com.k4ln.debug4j.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.k4ln.debug4j.common.response.HttpResult;
import com.k4ln.debug4j.common.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SaConfiguration {

    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                .addInclude("/**").addExclude("/favicon.ico")
                .setAuth(obj -> SaRouter.match("/**", () -> {
                    SaHttpBasicUtil.check();
                    try {
                        StpUtil.getLoginId();
                    } catch (Exception e) {
                        StpUtil.login(UUID.fastUUID().toString(true));
                    }
                }))
                .setError(e -> {
                    SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
                    return JSON.toJSONString(Result.result(HttpResult.NO_AUTH, e.getMessage()));
                });
    }
}
