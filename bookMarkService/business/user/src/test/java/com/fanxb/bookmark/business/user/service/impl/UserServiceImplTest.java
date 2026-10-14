package com.fanxb.bookmark.business.user.service.impl;

import com.fanxb.bookmark.business.api.BookmarkApi;
import com.fanxb.bookmark.business.user.dao.UserDao;
import com.fanxb.bookmark.business.user.service.SearchEngineService;
import com.fanxb.bookmark.business.user.vo.LoginBody;
import com.fanxb.bookmark.business.user.vo.RegisterBody;
import com.fanxb.bookmark.common.constant.RedisConstant;
import com.fanxb.bookmark.common.dao.RoleDao;
import com.fanxb.bookmark.common.entity.po.User;
import com.fanxb.bookmark.common.exception.FormDataException;
import com.fanxb.bookmark.common.util.HashUtil;
import com.fanxb.bookmark.common.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 类功能简述：用户注册绑定超级管理员、登录禁用校验单测
 *
 * @author fanxb
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private BookmarkApi bookmarkApi;

    @Mock
    private RoleDao roleDao;

    @Mock
    private SearchEngineService searchEngineService;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDao, redisTemplate, bookmarkApi, roleDao);
        ReflectionTestUtils.setField(userService, "searchEngineService", searchEngineService);
    }

    @Test
    void testFirstUserBindSuperAdmin() {
        RegisterBody body = buildBody();
        when(userDao.selectByUsernameOrEmail("user1", "user1@test.com")).thenReturn(null);
        mockUserInsert();
        when(userDao.selectCount(null)).thenReturn(1L);
        when(roleDao.selectRoleIdByCode("super_admin")).thenReturn(9);

        try (MockedStatic<JwtUtil> jwtUtil = mockStatic(JwtUtil.class)) {
            jwtUtil.when(() -> JwtUtil.encode(anyMap(), anyString(), anyLong())).thenReturn("token");
            assertEquals("token", userService.register(body));
        }
        verify(roleDao).insertUserRole(eq(1), eq(9), anyLong());
    }

    @Test
    void testNotFirstUserNotBindSuperAdmin() {
        RegisterBody body = buildBody();
        when(userDao.selectByUsernameOrEmail("user1", "user1@test.com")).thenReturn(null);
        mockUserInsert();
        when(userDao.selectCount(null)).thenReturn(2L);

        try (MockedStatic<JwtUtil> jwtUtil = mockStatic(JwtUtil.class)) {
            jwtUtil.when(() -> JwtUtil.encode(anyMap(), anyString(), anyLong())).thenReturn("token");
            assertEquals("token", userService.register(body));
        }
        verify(roleDao, never()).selectRoleIdByCode(anyString());
        verify(roleDao, never()).insertUserRole(anyInt(), anyInt(), anyLong());
    }

    @Test
    void testDisabledUserLoginRejected() {
        String key = RedisConstant.getUserFailCountKey("user1");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);
        User user = new User();
        user.setUserId(1);
        user.setPassword(HashUtil.getPassword("123456"));
        user.setStatus(0);
        when(userDao.selectByUsernameOrEmail("user1", "user1")).thenReturn(user);

        LoginBody body = new LoginBody();
        body.setStr("user1");
        body.setPassword("123456");

        FormDataException e = assertThrows(FormDataException.class, () -> userService.login(body));
        assertEquals("账号已被禁用，请联系管理员", e.getMessage());
        verify(redisTemplate, never()).delete(anyString());
        verify(userDao, never()).updateLastLoginTime(anyLong(), anyInt());
    }

    private void mockUserInsert() {
        doAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(1);
            return null;
        }).when(userDao).addOne(any(User.class));
    }

    private RegisterBody buildBody() {
        RegisterBody body = new RegisterBody();
        body.setUsername("user1");
        body.setEmail("user1@test.com");
        body.setPassword("123456");
        return body;
    }
}
