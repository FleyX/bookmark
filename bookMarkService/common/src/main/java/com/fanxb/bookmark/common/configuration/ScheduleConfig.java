package com.fanxb.bookmark.common.configuration;

import com.fanxb.bookmark.common.factory.CustomThreadFactory;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created with IntelliJ IDEA
 *
 * @author fanxb
 * @date 2020/1/26
 */
public class ScheduleConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {

        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(5, new CustomThreadFactory("schedule"));
        scheduledTaskRegistrar.setScheduler(service);
    }

}
