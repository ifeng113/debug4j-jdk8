package com.k4ln.debug4j.controller;


import com.k4ln.debug4j.common.response.Result;
import com.k4ln.debug4j.controller.vo.ManageClientRespVO;
import com.k4ln.debug4j.controller.vo.ProxyReqVO;
import com.k4ln.debug4j.controller.vo.ProxyRespVO;
import com.k4ln.debug4j.service.ManageService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理接口类
 * @author k4ln
 * @date 2022/4/20 16:49
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/manage")
public class ManageController {

    @Resource
    ManageService manageService;

    /**
     * 获取客户端
     * @return
     */
    @GetMapping("/clients")
    public Result<List<ManageClientRespVO>> clients() {
        return Result.ok(manageService.getClients());
    }

}
