package com.fanxb.bookmark.common.filter;

import com.auth0.jwt.interfaces.Claim;
import com.fanxb.bookmark.common.constant.CommonConstant;
import com.fanxb.bookmark.common.entity.Result;
import com.fanxb.bookmark.common.entity.UserContext;
import com.fanxb.bookmark.common.exception.NoLoginException;
import com.fanxb.bookmark.common.permission.PermissionRegistry;
import com.fanxb.bookmark.common.service.LoginUserLogService;
import com.fanxb.bookmark.common.service.PermissionService;
import com.fanxb.bookmark.common.service.UserStateService;
import com.fanxb.bookmark.common.util.JsonUtil;
import com.fanxb.bookmark.common.util.JwtUtil;
import com.fanxb.bookmark.common.util.StringUtil;
import com.fanxb.bookmark.common.util.UserContextHolder;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 类功能简述：登录过滤器，三段式鉴权：公共放行 -> 登录校验 -> 角色资源匹配
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/5 16:07
 */

@Component
@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "loginFilter")
@Order(1000)
@RequiredArgsConstructor
public class LoginFilter implements Filter {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private static final String NO_PERMISSION_MESSAGE = "无权限访问";

    @Value("${server.servlet.context-path}")
    private String urlPrefix;

    @Value("${jwtSecret}")
    private String secret;

    private final PermissionRegistry permissionRegistry;
    private final PermissionService permissionService;
    private final LoginUserLogService loginUserLogService;
    private final UserStateService userStateService;

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 指定允许其他域名访问
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Max-Age", "0");
        // 响应类型
        response.setHeader("Access-Control-Allow-Methods", "POST,GET,DELETE,OPTIONS,DELETE");
        // 响应头设置
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,jwt-token");

        // 如果是option请求直接返回200
        String requestMethod = request.getMethod();
        if (requestMethod.equalsIgnoreCase(HttpMethod.OPTIONS.name())) {
            response.setStatus(HttpStatus.OK.value());
            return;
        }

        String requestUrl = request.getRequestURI().replace(urlPrefix, "");
        //放行静态文件
        if (requestUrl.startsWith("/static")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String resourceKey = requestMethod.toUpperCase(Locale.ROOT) + ":" + requestUrl;
        //解析jwt填充用户上下文，公共url不强制登录，解析失败不影响放行
        boolean login = this.checkJwt(request.getHeader(CommonConstant.JWT_KEY));
        try {
            //检查是否公共方法
            if (this.matchAny(permissionRegistry.getPublicPatterns(), resourceKey)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            if (!login) {
                this.writeResult(response, new Result(NoLoginException.CODE, NoLoginException.MESSAGE, null));
                return;
            }
            //禁用账号、管理员重置密码后，旧 JWT 按未登录处理
            UserContext userContext = UserContextHolder.get();
            if (!userStateService.checkUserState(userContext.getUserId(), userContext.getJwtIssuedAt())) {
                this.writeResult(response, new Result(NoLoginException.CODE, NoLoginException.MESSAGE, null));
                return;
            }
            //受权限控制的接口需要用户资源匹配
            if (permissionRegistry.isProtected(resourceKey) && !this.hasPermission(resourceKey)) {
                this.writeResult(response, new Result(CommonConstant.NO_PERMISSION_CODE, NO_PERMISSION_MESSAGE, null));
                return;
            }
            loginUserLogService.logToRedis();
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContextHolder.remove();
        }
    }

    private boolean hasPermission(String resourceKey) {
        int userId = UserContextHolder.get().getUserId();
        if (permissionService.isSuperAdmin(userId)) {
            return true;
        }
        Set<String> resourceCodes = permissionService.resolveResourceCodes(userId);
        return resourceCodes != null && this.matchAny(resourceCodes, resourceKey);
    }

    private boolean matchAny(Iterable<String> patterns, String resourceKey) {
        for (String pattern : patterns) {
            if (MATCHER.match(pattern, resourceKey)) {
                return true;
            }
        }
        return false;
    }

    private void writeResult(HttpServletResponse response, Result result) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JsonUtil.obj2String(result));
    }

    private boolean checkJwt(String jwt) {
        if (StringUtil.isEmpty(jwt)) {
            return false;
        }
        try {
            Map<String, Claim> map = JwtUtil.decode(jwt, secret);
            int userId = Integer.parseInt(map.get("userId").asString());
            UserContext context = new UserContext();
            context.setJwt(jwt);
            context.setUserId(userId);
            Claim issuedAt = map.get("iat");
            context.setJwtIssuedAt(issuedAt == null ? null : issuedAt.asDate());
            UserContextHolder.set(context);
            return true;
        } catch (Exception e) {
            log.info("jwt解密失败：{},原因：{}", jwt, e.getMessage());
            return false;
        }
    }
}
