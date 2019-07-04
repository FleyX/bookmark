package com.fanxb.business.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/4 8:59
 */
@RestController
@RequestMapping("/user")
public class User {

    @GetMapping("test")
    public String getName() {
        return "test";
    }
}
