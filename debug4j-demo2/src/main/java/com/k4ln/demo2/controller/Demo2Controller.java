package com.k4ln.demo2.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 服务端接口类
 *
 * @author k4ln
 * @date 2022/4/20 16:49
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/demo2")
public class Demo2Controller {

    @GetMapping
    public String demo2() {
        return "demo2";
    }
}
