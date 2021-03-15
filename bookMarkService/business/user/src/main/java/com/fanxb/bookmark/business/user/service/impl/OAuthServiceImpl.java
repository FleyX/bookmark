package com.fanxb.bookmark.business.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.fanxb.bookmark.business.user.dao.UserDao;
import com.fanxb.bookmark.business.user.service.OAuthService;
import com.fanxb.bookmark.business.user.service.UserService;
import com.fanxb.bookmark.business.user.vo.OAuthBody;
import com.fanxb.bookmark.common.constant.Constant;
import com.fanxb.bookmark.common.entity.User;
import com.fanxb.bookmark.common.exception.CustomException;
import com.fanxb.bookmark.common.util.HttpUtil;
import com.fanxb.bookmark.common.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.fanxb.bookmark.business.user.service.UserService.LONG_EXPIRE_TIME;
import static com.fanxb.bookmark.business.user.service.UserService.SHORT_EXPIRE_TIME;

/**
 * OAuth交互类
 *
 * @author fanxb
 * @date 2021/3/10
 **/
@Service
@Slf4j
public class OAuthServiceImpl implements OAuthService {

    @Value("${OAuth.github.clientId}")
    private String githubClientId;
    @Value("${OAuth.github.secret}")
    private String githubSecret;
    private final UserDao userDao;
    private final UserService userService;

    @Autowired
    public OAuthServiceImpl(UserDao userDao, UserService userService) {
        this.userDao = userDao;
        this.userService = userService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String oAuthCheck(OAuthBody body) {
        User current, other = new User();
        if (StrUtil.equals(body.getType(), OAuthBody.GITHUB)) {
            Map<String, String> header = new HashMap<>(2);
            header.put("accept", "application/json");
            String url = "https://github.com/login/oauth/access_token?client_id=" + githubClientId + "&client_secret=" + githubSecret + "&code=" + body.getCode();
            JSONObject obj = HttpUtil.getObj(url, header, true);
            String accessToken = obj.getString("access_token");
            if (StrUtil.isEmpty(accessToken)) {
                throw new CustomException("github登陆失败，请稍后重试");
            }
            header.put("Authorization", "token " + accessToken);
            JSONObject userInfo = HttpUtil.getObj("https://api.github.com/user", header, true);
            other.setGithubId(userInfo.getLong("id"));
            if (other.getGithubId() == null) {
                log.error("github返回异常:{}", userInfo.toString());
                throw new CustomException("登陆异常，请稍后重试");
            }
            other.setEmail(userInfo.getString("email"));
            other.setIcon(userInfo.getString("avatar_url"));
            other.setUsername(userInfo.getString("login"));
            current = userDao.selectByUserIdOrGithubId(null, other.getGithubId());
            if (current == null) {
                current = userDao.selectByUsernameOrEmail(null, other.getEmail());
            }
        } else {
            throw new CustomException("不支持的登陆方式" + body.getType());
        }
        User newest = dealOAuth(current, other);
        return JwtUtil.encode(Collections.singletonMap("userId", String.valueOf(newest.getUserId())), Constant.jwtSecret
                , body.isRememberMe() ? LONG_EXPIRE_TIME : SHORT_EXPIRE_TIME);
    }

    /**
     * TODO 方法描述
     *
     * @param current 当前是否存在该用户
     * @param other   第三方获取的数据
     * @return User 最新的用户信息
     * @author fanxb
     * @date 2021/3/11
     **/
    private User dealOAuth(User current, User other) {
        if (current == null) {
            //判断用户名是否可用
            if (userDao.usernameExist(other.getUsername())) {
                other.setUsername(userService.createNewUsername());
            }
            other.setPassword("");
            other.setCreateTime(System.currentTimeMillis());
            other.setLastLoginTime(System.currentTimeMillis());
            other.setIcon(UserService.DEFAULT_ICON);
            other.setCreateTime(System.currentTimeMillis());
            other.setLastLoginTime(System.currentTimeMillis());
            other.setVersion(0);
            userDao.addOne(other);
            return other;
        } else {
            if (!current.getEmail().equals(other.getEmail()) || !current.getGithubId().equals(other.getGithubId())) {
                current.setEmail(other.getEmail());
                current.setGithubId(other.getGithubId());
                userDao.updateEmailAndGithubId(current);
            }
            return current;
        }
    }
}
