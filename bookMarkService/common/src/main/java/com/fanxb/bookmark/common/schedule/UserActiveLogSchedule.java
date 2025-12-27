package com.fanxb.bookmark.common.schedule;

import com.fanxb.bookmark.common.service.ConfigService;
import com.fanxb.bookmark.common.service.LoginUserLogService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author fanxb
 */
@Component
@RequiredArgsConstructor
public class UserActiveLogSchedule {
    private final LoginUserLogService loginUserLogService;

    @PostConstruct
    @Scheduled(cron = "0 0/5 * * * *")
    public void cache() {
        loginUserLogService.saveToDb();
    }

}
