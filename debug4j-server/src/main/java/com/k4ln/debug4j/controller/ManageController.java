package com.k4ln.debug4j.controller;


import com.k4ln.debug4j.common.response.Result;
import com.k4ln.debug4j.controller.vo.ManageClientRespVO;
import com.k4ln.debug4j.service.ManageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理接口类
 *
 * @author k4ln
 * @since 2024-10-22
 */
@Slf4j
@RestController
@RequestMapping("/manage")
public class ManageController {

    @Resource
    ManageService manageService;

    /**
     * 获取客户端
     *
     * @return
     */
    @GetMapping("/clients")
    public Result<List<ManageClientRespVO>> clients() {
        return Result.ok(manageService.getClients());
    }

}
