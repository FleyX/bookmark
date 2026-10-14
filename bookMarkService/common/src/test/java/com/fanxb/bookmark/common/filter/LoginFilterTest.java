package com.fanxb.bookmark.common.filter;

import com.fanxb.bookmark.common.constant.CommonConstant;
import com.fanxb.bookmark.common.permission.PermissionRegistry;
import com.fanxb.bookmark.common.service.LoginUserLogService;
import com.fanxb.bookmark.common.service.PermissionService;
import com.fanxb.bookmark.common.service.UserStateService;
import com.fanxb.bookmark.common.util.JwtUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 类功能简述：登录过滤器三段式鉴权单测
 *
 * @author fanxb
 */
@ExtendWith(MockitoExtension.class)
class LoginFilterTest {

    private static final String SECRET = "test-secret";

    private static final String URL_PREFIX = "/bookmark/api";

    @Mock
    private PermissionRegistry permissionRegistry;

    @Mock
    private PermissionService permissionService;

    @Mock
    private LoginUserLogService loginUserLogService;

    @Mock
    private UserStateService userStateService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private LoginFilter loginFilter;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        loginFilter = new LoginFilter(permissionRegistry, permissionService, loginUserLogService, userStateService);
        ReflectionTestUtils.setField(loginFilter, "urlPrefix", URL_PREFIX);
        ReflectionTestUtils.setField(loginFilter, "secret", SECRET);
        responseWriter = new StringWriter();
        lenient().when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        //默认用户状态正常，仅状态相关用例覆盖
        lenient().when(userStateService.checkUserState(anyInt(), any())).thenReturn(true);
    }

    @Test
    void testPublicUrlPass() throws Exception {
        mockRequest("POST", "/user/login", null);
        when(permissionRegistry.getPublicPatterns()).thenReturn(List.of("POST:/user/login"));

        loginFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(loginUserLogService, never()).logToRedis();
        assertEquals("", responseWriter.toString());
    }

    @Test
    void testNoJwtReturnNoLogin() throws Exception {
        mockRequest("GET", "/bookmark/currentUser", null);
        when(permissionRegistry.getPublicPatterns()).thenReturn(List.of());

        loginFilter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
        assertTrue(responseWriter.toString().contains("-1"));
        verify(loginUserLogService, never()).logToRedis();
    }

    @Test
    void testUnregisteredUrlPassAfterLogin() throws Exception {
        mockRequest("GET", "/bookmark/currentUser", jwt(1));
        when(permissionRegistry.getPublicPatterns()).thenReturn(List.of());
        when(permissionRegistry.isProtected("GET:/bookmark/currentUser")).thenReturn(false);

        loginFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(loginUserLogService).logToRedis();
        verify(permissionService, never()).isSuperAdmin(anyInt());
    }

    @Test
    void testProtectedUrlNoPermission() throws Exception {
        mockRequest("GET", "/user/manage/page", jwt(1));
        when(permissionRegistry.getPublicPatterns()).thenReturn(List.of());
        when(permissionRegistry.isProtected("GET:/user/manage/page")).thenReturn(true);
        when(permissionService.isSuperAdmin(1)).thenReturn(false);
        when(permissionService.resolveResourceCodes(1)).thenReturn(Set.of());

        loginFilter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
        assertTrue(responseWriter.toString().contains("-3"));
        verify(loginUserLogService, never()).logToRedis();
    }

    @Test
    void testProtectedUrlHasPermission() throws Exception {
        mockRequest("GET", "/user/manage/page", jwt(1));
        when(permissionRegistry.getPublicPatterns()).thenReturn(List.of());
        when(permissionRegistry.isProtected("GET:/user/manage/page")).thenReturn(true);
        when(permissionService.isSuperAdmin(1)).thenReturn(false);
        when(permissionService.resolveResourceCodes(1)).thenReturn(Set.of("GET:/user/manage/page"));

        loginFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(loginUserLogService).logToRedis();
    }

    @Test
    void testSuperAdminShortCircuit() throws Exception {
        mockRequest("GET", "/user/manage/page", jwt(1));
        when(permissionRegistry.getPublicPatterns()).thenReturn(List.of());
        when(permissionRegistry.isProtected("GET:/user/manage/page")).thenReturn(true);
        when(permissionService.isSuperAdmin(1)).thenReturn(true);

        loginFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(permissionService, never()).resolveResourceCodes(anyInt());
    }

    @Test
    void testDisabledUserReturnNoLogin() throws Exception {
        mockRequest("GET", "/bookmark/currentUser", jwt(1));
        when(permissionRegistry.getPublicPatterns()).thenReturn(List.of());
        when(userStateService.checkUserState(eq(1), any())).thenReturn(false);

        loginFilter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
        assertTrue(responseWriter.toString().contains("-1"));
        verify(loginUserLogService, never()).logToRedis();
    }

    @Test
    void testOldJwtAfterPasswordResetReturnNoLogin() throws Exception {
        long resetTime = System.currentTimeMillis();
        mockRequest("GET", "/bookmark/currentUser", jwtWithIssuedAt(1, new Date(resetTime - 10_000)));
        when(permissionRegistry.getPublicPatterns()).thenReturn(List.of());
        //模拟真实服务契约：签发时间早于重置时间即失效
        when(userStateService.checkUserState(eq(1), argThat(iat -> iat != null && iat.getTime() < resetTime)))
                .thenReturn(false);

        loginFilter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(ServletRequest.class), any(ServletResponse.class));
        assertTrue(responseWriter.toString().contains("-1"));
    }

    @Test
    void testNewJwtPassUserStateCheck() throws Exception {
        mockRequest("GET", "/bookmark/currentUser", jwt(1));
        when(permissionRegistry.getPublicPatterns()).thenReturn(List.of());

        loginFilter.doFilter(request, response, filterChain);

        ArgumentCaptor<Date> captor = ArgumentCaptor.forClass(Date.class);
        verify(userStateService).checkUserState(eq(1), captor.capture());
        assertNotNull(captor.getValue());
        verify(filterChain).doFilter(request, response);
        verify(loginUserLogService).logToRedis();
    }

    private void mockRequest(String method, String uri, String jwt) {
        when(request.getMethod()).thenReturn(method);
        when(request.getRequestURI()).thenReturn(URL_PREFIX + uri);
        when(request.getHeader(CommonConstant.JWT_KEY)).thenReturn(jwt);
    }

    private String jwt(int userId) {
        return JwtUtil.encode(Map.of("userId", String.valueOf(userId)), SECRET, 3600_000L);
    }

    private String jwtWithIssuedAt(int userId, Date issuedAt) {
        return JWT.create()
                .withIssuedAt(issuedAt)
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600_000L))
                .withClaim("userId", String.valueOf(userId))
                .sign(Algorithm.HMAC256(SECRET));
    }
}
