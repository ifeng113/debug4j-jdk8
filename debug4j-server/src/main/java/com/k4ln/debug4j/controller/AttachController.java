package com.k4ln.debug4j.controller;


import com.k4ln.debug4j.common.response.Result;
import com.k4ln.debug4j.controller.vo.AttachClassAllReqVO;
import com.k4ln.debug4j.controller.vo.AttachClassSourceReqVO;
import com.k4ln.debug4j.service.AttachService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 增强接口类
 * @author k4ln
 * @date 2022/4/20 16:49
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/attach")
public class AttachController {

    @Resource
    AttachService attachService;

    /**
     * 获取所有类
     * @return
     */
    @Validated
    @PostMapping("/class")
    public Result<List<String>> getClassNames(@RequestBody @Valid AttachClassAllReqVO attachClassAllReqVO) {
        return Result.ok(attachService.getClassNames(attachClassAllReqVO));
    }

    /**
     * 获取类源码
     * @param attachClassSourceReqVO
     * @return
     */
    @Validated
    @PostMapping("/source")
    public Result<String> getClassSource(@RequestBody @Valid AttachClassSourceReqVO attachClassSourceReqVO) {
        return Result.ok(attachService.getClassSource(attachClassSourceReqVO));
    }

}
