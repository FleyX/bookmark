package com.fanxb.bookmark.business.user.schedule;

import com.alibaba.fastjson.JSON;
import com.fanxb.bookmark.common.constant.RedisConstant;
import com.fanxb.bookmark.common.entity.redis.UserBookmarkUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author fanxb
 * @date 2020/1/26 上午11:54
 */
@Component
public class UserInfoUpdate {
    /**
     * 阻塞时间
     */
    private static final int BLOCK_TIME = 15;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Scheduled(fixedDelay = 5000)
    public void userBookmarkUpdateTime() {
        String value;
        while ((value = redisTemplate.opsForList().rightPop(RedisConstant.BOOKMARK_UPDATE_TIME, BLOCK_TIME, TimeUnit.SECONDS)) != null) {
            UserBookmarkUpdate item = JSON.parseObject(value, UserBookmarkUpdate.class);

        }
    }
}
