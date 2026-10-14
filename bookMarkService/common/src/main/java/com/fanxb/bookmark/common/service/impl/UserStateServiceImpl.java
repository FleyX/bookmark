package com.fanxb.bookmark.common.service.impl;

import com.fanxb.bookmark.common.constant.RedisConstant;
import com.fanxb.bookmark.common.service.UserStateService;
import com.fanxb.bookmark.common.util.StringUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类功能简述：用户状态服务实现，标记写 Redis，读取结果进程内缓存
 * 类功能详述：缓存 TTL 60s，写操作后主动 evict，单实例部署下即时生效
 *
 * @author fanxb
 */
@Service
@RequiredArgsConstructor
public class UserStateServiceImpl implements UserStateService {

    private static final String DISABLED_VALUE = "1";

    private static final long CACHE_TTL_MS = 60_000L;

    private final StringRedisTemplate redisTemplate;

    private final Map<Integer, State> cache = new ConcurrentHashMap<>();

    /**
     * 进程内缓存有效期，单测中通过反射缩短
     */
    private long cacheTtlMs = CACHE_TTL_MS;

    @Override
    public boolean checkUserState(int userId, Date jwtIssuedAt) {
        State state = getState(userId);
        if (state.isDisabled()) {
            return false;
        }
        Long pwdResetTime = state.getPwdResetTime();
        //无 iat 的旧 token 按签发时间 0 处理，重置过密码即失效
        return pwdResetTime == null || (jwtIssuedAt != null && jwtIssuedAt.getTime() >= pwdResetTime);
    }

    @Override
    public void disable(int userId) {
        redisTemplate.opsForValue().set(RedisConstant.getUserDisabledKey(userId), DISABLED_VALUE);
        this.evict(userId);
    }

    @Override
    public void enable(int userId) {
        redisTemplate.delete(RedisConstant.getUserDisabledKey(userId));
        this.evict(userId);
    }

    @Override
    public void markPasswordReset(int userId) {
        redisTemplate.opsForValue().set(RedisConstant.getPwdResetTimeKey(userId), String.valueOf(System.currentTimeMillis()));
        this.evict(userId);
    }

    @Override
    public void evict(int userId) {
        cache.remove(userId);
    }

    private State getState(int userId) {
        long now = System.currentTimeMillis();
        State cached = cache.get(userId);
        if (cached != null && cached.getExpireAt() > now) {
            return cached;
        }
        State state = loadState(userId);
        cache.put(userId, state);
        return state;
    }

    private State loadState(int userId) {
        boolean disabled = Boolean.TRUE.equals(redisTemplate.hasKey(RedisConstant.getUserDisabledKey(userId)));
        String resetTime = redisTemplate.opsForValue().get(RedisConstant.getPwdResetTimeKey(userId));
        Long pwdResetTime = StringUtil.isEmpty(resetTime) ? null : Long.parseLong(resetTime);
        return new State(disabled, pwdResetTime, System.currentTimeMillis() + cacheTtlMs);
    }

    /**
     * 用户状态缓存内容
     */
    @Data
    private static class State {

        private final boolean disabled;

        private final Long pwdResetTime;

        private final long expireAt;
    }
}
