package com.fanxb.bookmark.common.service.impl;

import com.fanxb.bookmark.common.constant.RedisConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 类功能简述：用户状态服务单测
 *
 * @author fanxb
 */
@ExtendWith(MockitoExtension.class)
class UserStateServiceImplTest {

    private static final int USER_ID = 1;

    private static final String DISABLED_KEY = RedisConstant.getUserDisabledKey(USER_ID);

    private static final String PWD_RESET_KEY = RedisConstant.getPwdResetTimeKey(USER_ID);

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private UserStateServiceImpl userStateService;

    @BeforeEach
    void setUp() {
        userStateService = new UserStateServiceImpl(redisTemplate);
    }

    @Test
    void testDisabledUserReturnFalse() {
        when(redisTemplate.hasKey(DISABLED_KEY)).thenReturn(true);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(PWD_RESET_KEY)).thenReturn(null);

        assertFalse(userStateService.checkUserState(USER_ID, new Date()));
    }

    @Test
    void testNoMarkPass() {
        when(redisTemplate.hasKey(DISABLED_KEY)).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(PWD_RESET_KEY)).thenReturn(null);

        //无任何标记时，无 iat 的旧 token 也放行
        assertTrue(userStateService.checkUserState(USER_ID, null));
    }

    @Test
    void testOldJwtAfterPasswordResetReturnFalse() {
        when(redisTemplate.hasKey(DISABLED_KEY)).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(PWD_RESET_KEY)).thenReturn("2000");

        assertFalse(userStateService.checkUserState(USER_ID, new Date(1999)));
    }

    @Test
    void testNewJwtAfterPasswordResetPass() {
        when(redisTemplate.hasKey(DISABLED_KEY)).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(PWD_RESET_KEY)).thenReturn("2000");

        assertTrue(userStateService.checkUserState(USER_ID, new Date(2000)));
        assertTrue(userStateService.checkUserState(USER_ID, new Date(3000)));
    }

    @Test
    void testMissingIatAfterPasswordResetReturnFalse() {
        when(redisTemplate.hasKey(DISABLED_KEY)).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(PWD_RESET_KEY)).thenReturn("2000");

        //旧 token 无 iat，按签发时间 0 处理
        assertFalse(userStateService.checkUserState(USER_ID, null));
    }

    @Test
    void testCacheHitWithinTtl() {
        when(redisTemplate.hasKey(DISABLED_KEY)).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(PWD_RESET_KEY)).thenReturn(null);

        userStateService.checkUserState(USER_ID, null);
        userStateService.checkUserState(USER_ID, null);

        //60s 内只读一次 Redis
        verify(redisTemplate, times(1)).hasKey(DISABLED_KEY);
    }

    @Test
    void testCacheExpiredReload() {
        ReflectionTestUtils.setField(userStateService, "cacheTtlMs", 0L);
        when(redisTemplate.hasKey(DISABLED_KEY)).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(PWD_RESET_KEY)).thenReturn(null);

        userStateService.checkUserState(USER_ID, null);
        userStateService.checkUserState(USER_ID, null);

        verify(redisTemplate, times(2)).hasKey(DISABLED_KEY);
    }

    @Test
    void testEvictReload() {
        when(redisTemplate.hasKey(DISABLED_KEY)).thenReturn(false);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(PWD_RESET_KEY)).thenReturn(null);

        userStateService.checkUserState(USER_ID, null);
        userStateService.checkUserState(USER_ID, null);
        userStateService.evict(USER_ID);
        userStateService.checkUserState(USER_ID, null);

        verify(redisTemplate, times(2)).hasKey(DISABLED_KEY);
    }

    @Test
    void testDisableWriteRedisAndEvictCache() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(PWD_RESET_KEY)).thenReturn(null);
        //缓存一份正常状态，禁用后应立即失效并重新读取 Redis
        when(redisTemplate.hasKey(DISABLED_KEY)).thenReturn(false, true);

        assertTrue(userStateService.checkUserState(USER_ID, null));

        userStateService.disable(USER_ID);

        verify(valueOperations).set(DISABLED_KEY, "1");
        assertFalse(userStateService.checkUserState(USER_ID, null));
        verify(redisTemplate, times(2)).hasKey(DISABLED_KEY);
    }

    @Test
    void testEnableDeleteRedisAndEvictCache() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(PWD_RESET_KEY)).thenReturn(null);
        when(redisTemplate.hasKey(DISABLED_KEY)).thenReturn(true, false);

        assertFalse(userStateService.checkUserState(USER_ID, null));

        userStateService.enable(USER_ID);

        verify(redisTemplate).delete(DISABLED_KEY);
        assertTrue(userStateService.checkUserState(USER_ID, null));
    }

    @Test
    void testMarkPasswordResetWriteTimestampAndEvictCache() {
        long resetTime = System.currentTimeMillis();
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.hasKey(DISABLED_KEY)).thenReturn(false);
        when(valueOperations.get(PWD_RESET_KEY)).thenReturn(null, String.valueOf(resetTime + 100_000));

        assertTrue(userStateService.checkUserState(USER_ID, null));

        userStateService.markPasswordReset(USER_ID);

        verify(valueOperations).set(eq(PWD_RESET_KEY), anyString());
        //重置标记生效后，无 iat 的旧 token 一律失效
        assertFalse(userStateService.checkUserState(USER_ID, null));
    }
}
