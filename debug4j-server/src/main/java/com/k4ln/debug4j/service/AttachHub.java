package com.k4ln.debug4j.service;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AttachHub {

    /**
     * reqId -> 异步任务
     */
    private final TimedCache<String, CompletableFuture<String>> attachTask = CacheUtil.newTimedCache(60 * 1000);

    /**
     * 同步等待
     * @param reqId
     * @return
     */
    public <T> T syncResult(String reqId, Runnable runnable, Class<T> clazz) {
        CompletableFuture<String> future = new CompletableFuture<>();
        attachTask.put(reqId, future);
        try {
            runnable.run();
            String result = future.get(30, TimeUnit.SECONDS);
            attachTask.remove(reqId);
            return JSON.parseObject(result, clazz);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 推送数据
     * @param reqId
     * @param data
     */
    public void pushResult(String reqId, String data) {
        CompletableFuture<String> future = attachTask.get(reqId);
        if (future != null){
            future.complete(data);
        }
    }
}
