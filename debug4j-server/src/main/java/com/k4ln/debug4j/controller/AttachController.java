package com.k4ln.debug4j.controller;


import cn.dev33.satoken.stp.StpUtil;
import com.k4ln.debug4j.common.response.Result;
import com.k4ln.debug4j.controller.vo.*;
import com.k4ln.debug4j.service.AttachHub;
import com.k4ln.debug4j.service.AttachService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * 增强接口类
 *
 * @author k4ln
 * @date 2022/4/20 16:49
 * @description
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/attach")
public class AttachController {

    @Resource
    AttachService attachService;

    @Resource
    AttachHub attachHub;

    /**
     * 获取所有类
     *
     * @return
     */
    @PostMapping("/class")
    public Result<List<String>> getClassNames(@RequestBody @Valid AttachClassAllReqVO attachClassAllReqVO) {
        return Result.ok(attachService.getClassNames(attachClassAllReqVO));
    }

    /**
     * 获取类源码
     *
     * @param attachClassSourceReqVO
     * @return
     */
    @PostMapping("/source")
    public Result<AttachClassSourceRespVO> getClassSource(@RequestBody @Valid AttachClassSourceReqVO attachClassSourceReqVO) {
        return Result.ok(attachService.getClassSource(attachClassSourceReqVO));
    }

    /**
     * 源码热更新
     *
     * @param sourceReloadReqVO
     * @return
     */
    @PostMapping("/reload/source")
    public Result<String> sourceReload(@RequestBody @Valid AttachSourceReloadReqVO sourceReloadReqVO) {
        return Result.ok(attachService.sourceReload(sourceReloadReqVO));
    }

    /**
     * 字节码热更新
     *
     * @param classFile
     * @param clientSessionId
     * @param className
     * @return
     */
    @PostMapping(value = "/reload/class", consumes = "multipart/form-data")
    public Result<String> classReload(@RequestParam("file") MultipartFile classFile,
                                      @RequestParam("clientSessionId") String clientSessionId,
                                      @RequestParam("className") String className) {
        return Result.ok(attachService.classReload(classFile, clientSessionId, className));
    }

    /**
     * 代码还原
     *
     * @param classRestoreReqVO
     * @return
     */
    @PostMapping("/restore")
    public Result<String> classRestore(@RequestBody @Valid AttachClassRestoreReqVO classRestoreReqVO) {
        return Result.ok(attachService.classRestore(classRestoreReqVO));
    }

    /**
     * 获取任务列表
     *
     * @param attachTaskReqVO
     * @return
     */
    @PostMapping("/task")
    public Result<List<AttachTaskRespVO>> getTask(@RequestBody @Valid AttachTaskReqVO attachTaskReqVO) {
        return Result.ok(attachService.getTask(attachTaskReqVO));
    }

    /**
     * 开启任务
     *
     * @param openReqVO
     * @return
     */
    @PostMapping("/task/open")
    public Result<List<AttachTaskRespVO>> openTask(@RequestBody @Valid AttachTaskOpenReqVO openReqVO) {
        return Result.ok(attachService.openTask(openReqVO));
    }

    /**
     * 关闭任务
     *
     * @param closeReqVO
     * @return
     */
    @PostMapping("/task/close")
    public Result<List<AttachTaskRespVO>> closeTask(@RequestBody @Valid AttachTaskCloseReqVO closeReqVO) {
        return Result.ok(attachService.closeTask(closeReqVO));
    }

    /**
     * 获取任务详情
     *
     * @param path
     * @return
     */
    @GetMapping("/task")
    public SseEmitter getTaskDetails(@RequestParam("path") String path, @RequestParam("sessionId") String sessionId) {
        return attachHub.getSseEmitter(sessionId + "@" + path, (String) StpUtil.getLoginId());
    }

}
