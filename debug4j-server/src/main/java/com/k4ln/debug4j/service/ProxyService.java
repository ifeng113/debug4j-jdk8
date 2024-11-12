package com.k4ln.debug4j.service;

import cn.hutool.core.net.NetUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.k4ln.debug4j.config.SocketServerProperties;
import com.k4ln.debug4j.controller.vo.ProxyReqVO;
import com.k4ln.debug4j.controller.vo.ProxyRespVO;
import com.k4ln.debug4j.response.exception.BaseException;
import com.k4ln.debug4j.response.exception.abort.BusinessAbort;
import com.k4ln.debug4j.socket.SocketServer;
import com.k4ln.debug4j.socket.SocketTFProxyServer;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.BindException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ProxyService {

    @Resource
    SocketServer socketServer;

    @Resource
    SocketServerProperties serverProperties;

    @Resource
    HttpServletRequest httpServletRequest;

    /**
     * ProxyReqVO -> SocketTFProxyServer
     */
    private final Map<String, SocketTFProxyServer> proxyServers = new ConcurrentHashMap<>();

    /**
     * 创建代理
     *
     * @param proxyReqVO
     * @return
     */
    public ProxyRespVO proxy(ProxyReqVO proxyReqVO) {
        if (!socketServer.getSessionMap().containsKey(proxyReqVO.getSocketClient())) {
            throw new BusinessAbort("not found remote client");
        }
        String proxyKey = getProxyKey(proxyReqVO);
        if (proxyServers.containsKey(proxyKey)) {
            SocketTFProxyServer proxyServer = proxyServers.get(proxyKey);
            return ProxyRespVO.builder().proxyPort(proxyServer.getTfpServerPort()).build();
        } else {
            try {
                if (proxyReqVO.getServerPort() == null){
                    proxyReqVO.setServerPort(NetUtil.getUsableLocalPort(serverProperties.getMinProxyPort(), serverProperties.getMaxProxyPort()));
                }
                if (proxyReqVO.getAllowNetworks() == null || proxyReqVO.getAllowNetworks().isEmpty()) {
                    String clientIP = JakartaServletUtil.getClientIP(httpServletRequest);
                    List<String> allowNetworks = new ArrayList<>();
                    allowNetworks.add(clientIP + "/32");
                    proxyReqVO.setAllowNetworks(allowNetworks);
                }
                SocketTFProxyServer proxyServer = new SocketTFProxyServer(proxyReqVO.getServerPort(), proxyReqVO, socketServer);
                proxyServer.start();
                proxyServers.put(proxyKey, proxyServer);
                log.info("proxy server:{} started at port {}", proxyKey, proxyReqVO.getServerPort());
                return ProxyRespVO.builder().proxyPort(proxyServer.getTfpServerPort()).allowNetworks(proxyReqVO.getAllowNetworks()).build();
            } catch (BindException e) {
                throw new BusinessAbort("port already in use");
            } catch (Exception e) {
                throw new BaseException(e.getMessage());
            }
        }
    }

    /**
     * 获取
     * @param proxyReqVO
     * @return
     */
    private static String getRemoteKey(ProxyReqVO proxyReqVO) {
        return proxyReqVO.getRemoteHost() + ":" + proxyReqVO.getRemotePort();
    }

    /**
     * 获取代理Key
     *
     * @param proxyReqVO
     * @return
     */
    public static String getProxyKey(ProxyReqVO proxyReqVO) {
        return proxyReqVO.getSocketClient() + "#" + proxyReqVO.getRemoteHost() + "#" + proxyReqVO.getRemotePort();
    }
}
