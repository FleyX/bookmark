package com.fanxb.bookmark.common.controller;

import com.fanxb.bookmark.common.entity.Result;
import com.fanxb.bookmark.common.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fanxb
 * @date 2021-09-15-下午9:55
 */
@RestController
@RequestMapping("/common/config")
public class ConfigController {

    private final ConfigService configService;

    @Autowired
    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * 获取全局配置
     *
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2021/9/15 下午9:56
     */
    @GetMapping("/global")
    public Result getGlobalConfig() {
        return Result.success(configService.getGlobalConfig());
    }
}
