package com.fanxb.bookmark.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fanxb.bookmark.common.constant.CommonConstant;
import com.fanxb.bookmark.common.constant.RedisConstant;
import com.fanxb.bookmark.common.dao.CommonUserDao;
import com.fanxb.bookmark.common.entity.UserContext;
import com.fanxb.bookmark.common.entity.po.User;
import com.fanxb.bookmark.common.service.LoginUserLogService;
import com.fanxb.bookmark.common.util.UserContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginUserLogServiceImpl implements LoginUserLogService {
    private final RedissonClient redissonClient;
    private final StringRedisTemplate redisTemplate;
    private final CommonUserDao userDao;

    @Override
    public void logToRedis() {
        try {
            RScoredSortedSet<Object> zSet = redissonClient.getScoredSortedSet(RedisConstant.USER_USE_TIME_SET);
            zSet.add(System.currentTimeMillis(), UserContextHolder.get().getUserId());
        } catch (Exception e) {
            log.error("[logToRedis]error", e);
        }
    }

    @Override
    public void saveToDb() {
        RLock lock = redissonClient.getLock(RedisConstant.USER_USE_TIME_SAVE_DB_TIME_UNI);
        try {
            if (!lock.tryLock()) {
                return;
            }
            log.info("[saveToDb run]");
            String val = redisTemplate.opsForValue().get(RedisConstant.USER_USE_TIME_SAVE_DB_TIME);
            Long startTime, endTime = System.currentTimeMillis();
            startTime = val == null ? endTime - 24 * 60 * 60 * 1000 : Long.parseLong(val);
            RScoredSortedSet<Object> zSet = redissonClient.getScoredSortedSet(RedisConstant.USER_USE_TIME_SET);
            Collection<ScoredEntry<Object>> entries = zSet.entryRange(startTime.doubleValue(), true, endTime.doubleValue(), false);
            entries.forEach(item -> userDao.update(new LambdaUpdateWrapper<User>()
                    .set(User::getLastActiveTime, new Date(item.getScore().longValue()))
                    .eq(User::getUserId, item.getValue())
            ));
            zSet.removeAll(entries.stream().map(ScoredEntry::getValue).collect(Collectors.toList()));
            redisTemplate.opsForValue().set(RedisConstant.USER_USE_TIME_SAVE_DB_TIME, endTime.toString());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
