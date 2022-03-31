package com.fanxb.bookmark.common.schedule;

import com.fanxb.bookmark.common.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author fanxb
 */
@Component
public class BingImgSchedule {
    private final ConfigService configService;

    @Autowired
    public BingImgSchedule(ConfigService configService) {
        this.configService = configService;
    }

    @PostConstruct
    @Scheduled(cron = "* 0 0/1 * *  *")
    public void cache() {
        configService.getCacheBingImg();
    }

}
