package com.fanxb.bookmark.business.user.service.impl;

import com.fanxb.bookmark.business.user.dao.UserDao;
import com.fanxb.bookmark.business.user.service.SearchEngineService;
import com.fanxb.bookmark.business.user.service.UserService;
import com.fanxb.bookmark.business.user.vo.OauthBody;
import com.fanxb.bookmark.common.entity.po.User;
import com.fanxb.bookmark.common.exception.FormDataException;
import com.fanxb.bookmark.common.util.HttpUtil;
import com.fanxb.bookmark.common.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 类功能简述：OAuth 登录单测
 *
 * @author fanxb
 */
@ExtendWith(MockitoExtension.class)
class OauthServiceImplTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Mock
    private UserDao userDao;

    @Mock
    private UserService userService;

    @Mock
    private SearchEngineService searchEngineService;

    private OauthServiceImpl oauthService;

    @BeforeEach
    void setUp() {
        oauthService = new OauthServiceImpl(userDao, userService);
        ReflectionTestUtils.setField(oauthService, "searchEngineService", searchEngineService);
        ReflectionTestUtils.setField(oauthService, "githubClientId", "id");
        ReflectionTestUtils.setField(oauthService, "githubSecret", "secret");
    }

    @Test
    void testDisabledUserOAuthLoginRejected() {
        User user = buildGithubUser(0);
        when(userDao.selectByUserIdOrGithubId(null, 123L)).thenReturn(user);

        try (MockedStatic<HttpUtil> httpUtil = mockStatic(HttpUtil.class)) {
            mockGithub(httpUtil);
            FormDataException e = assertThrows(FormDataException.class, () -> oauthService.oAuthCheck(buildBody()));
            assertEquals("账号已被禁用，请联系管理员", e.getMessage());
        }
    }

    @Test
    void testFirstOAuthUserBindSuperAdmin() {
        when(userDao.selectByUserIdOrGithubId(null, 123L)).thenReturn(null);
        when(userDao.selectByUsernameOrEmail(null, "oauth@test.com")).thenReturn(null);
        when(userDao.usernameExist("octocat")).thenReturn(false);
        doAnswer(invocation -> {
            invocation.getArgument(0, User.class).setUserId(1);
            return null;
        }).when(userDao).addOne(any(User.class));

        try (MockedStatic<HttpUtil> httpUtil = mockStatic(HttpUtil.class);
             MockedStatic<JwtUtil> jwtUtil = mockStatic(JwtUtil.class)) {
            mockGithub(httpUtil);
            jwtUtil.when(() -> JwtUtil.encode(anyMap(), anyString(), anyLong())).thenReturn("token");

            assertEquals("token", oauthService.oAuthCheck(buildBody()));
        }
        //新用户经OAuth注册同样视为首个注册用户，参与超管绑定
        verify(userService).bindSuperAdminIfAbsent(1);
    }

    private void mockGithub(MockedStatic<HttpUtil> httpUtil) {
        ObjectNode token = MAPPER.createObjectNode().put("access_token", "token123");
        ObjectNode userInfo = MAPPER.createObjectNode();
        userInfo.put("id", 123L);
        userInfo.put("email", "oauth@test.com");
        userInfo.put("avatar_url", "http://icon");
        userInfo.put("login", "octocat");
        httpUtil.when(() -> HttpUtil.getObj(anyString(), anyMap(), anyBoolean())).thenReturn(token, userInfo);
    }

    private User buildGithubUser(int status) {
        User user = new User();
        user.setUserId(2);
        user.setGithubId(123L);
        user.setEmail("oauth@test.com");
        user.setStatus(status);
        return user;
    }

    private OauthBody buildBody() {
        OauthBody body = new OauthBody();
        body.setType(OauthBody.GITHUB);
        body.setCode("code");
        return body;
    }
}
