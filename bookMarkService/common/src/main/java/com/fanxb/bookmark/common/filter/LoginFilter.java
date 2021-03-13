package com.fanxb.bookmark.common.filter;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.interfaces.Claim;
import com.fanxb.bookmark.common.constant.Constant;
import com.fanxb.bookmark.common.dao.UrlDao;
import com.fanxb.bookmark.common.entity.Result;
import com.fanxb.bookmark.common.entity.Url;
import com.fanxb.bookmark.common.entity.UserContext;
import com.fanxb.bookmark.common.exception.NoLoginException;
import com.fanxb.bookmark.common.util.JwtUtil;
import com.fanxb.bookmark.common.util.StringUtil;
import com.fanxb.bookmark.common.util.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/5 16:07
 */

@Component
@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "loginFilter")
@Order(1000)
public class LoginFilter implements Filter {

    @Value("${server.servlet.context-path}")
    private String urlPrefix;

    @Value("${manageUserId}")
    private int manageUserId;

    @Value("${jwtSecret}")
    private String secret;

    @Autowired
    private UrlDao urlDao;

    private static final AntPathMatcher matcher = new AntPathMatcher();

    volatile private static List<Url> publicUrl;

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
        List<Url> publicUrl = this.getPublicUrl();
        for (Url url : publicUrl) {
            if (url.getMethod().equalsIgnoreCase(requestMethod) && matcher.match(url.getUrl(), requestUrl)) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
        }
        //登陆用户
        if (this.checkJwt(request.getHeader(Constant.JWT_KEY))) {
            try {
                filterChain.doFilter(servletRequest, servletResponse);
            } finally {
                UserContextHolder.remove();
            }
        } else {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(JSON.toJSONString(new Result(NoLoginException.CODE, NoLoginException.MESSAGE, null)));
        }

    }

    private List<Url> getPublicUrl() {
        if (publicUrl == null) {
            synchronized (LoginFilter.class) {
                if (publicUrl == null) {
                    publicUrl = this.urlDao.getPublicUrl();
                }
            }
        }
        return publicUrl;
    }

    private boolean checkJwt(String jwt) {
        if (StringUtil.isEmpty(jwt)) {
            log.error("jwt为空");
            return false;
        }
        try {
            Map<String, Claim> map = JwtUtil.decode(jwt, secret);
            int userId = Integer.parseInt(map.get("userId").asString());
            UserContext context = new UserContext();
            context.setJwt(jwt);
            context.setUserId(userId);
            context.setManageUser(userId == manageUserId);
            UserContextHolder.set(context);
            return true;
        } catch (Exception e) {
            log.error("jwt解密失败：{},原因：{}", jwt, e.getMessage());
            return false;
        }
    }
}
