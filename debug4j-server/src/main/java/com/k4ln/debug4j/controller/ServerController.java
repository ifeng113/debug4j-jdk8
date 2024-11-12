package com.k4ln.debug4j.controller;


import com.k4ln.debug4j.controller.vo.ProxyReqVO;
import com.k4ln.debug4j.controller.vo.ProxyRespVO;
import com.k4ln.debug4j.response.Result;
import com.k4ln.debug4j.service.ProxyService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务端接口类
 * @author k4ln
 * @date 2022/4/20 16:49
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/server")
public class ServerController {

    @Resource
    ProxyService proxyService;

    /**
     * 创建代理
     * @param proxyReqVO
     * @return
     */
    @Validated // 不可作用于Controller，会使Controller变成非单例模式
    @PostMapping("proxy")
    private Result<ProxyRespVO> proxy(@RequestBody @Valid ProxyReqVO proxyReqVO) {
        return Result.ok(proxyService.proxy(proxyReqVO));
    }

}
