package com.fanxb.bookmark.business.user.service;

import com.fanxb.bookmark.business.user.dao.UserDao;
import com.fanxb.bookmark.business.user.entity.LoginBody;
import com.fanxb.bookmark.business.user.entity.LoginRes;
import com.fanxb.bookmark.business.user.entity.RegisterBody;
import com.fanxb.bookmark.common.Constant;
import com.fanxb.bookmark.common.entity.MailInfo;
import com.fanxb.bookmark.common.entity.User;
import com.fanxb.bookmark.common.exception.CustomException;
import com.fanxb.bookmark.common.exception.FormDataException;
import com.fanxb.bookmark.common.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/5 17:39
 */
@Service
public class UserService {

    private static final String DEFAULT_ICON = "defaultIcon.png";
    /**
     * 短期jwt失效时间
     */
    private static final long SHORT_EXPIRE_TIME = 2 * 60 * 60 * 1000;
    /**
     * 长期jwt失效时间
     */
    private static final long LONG_EXPIRE_TIME = 30 * TimeUtil.DAY_MS;

    @Autowired
    private UserDao userDao;

    /**
     * Description: 向目标发送验证码
     *
     * @param email 目标
     * @author fanxb
     * @date 2019/7/5 17:48
     */
    public void sendAuthCode(String email) {
        MailInfo info = new MailInfo();
        info.setSubject("签签世界注册验证码");
        int code = StringUtil.getRandomNumber(6, 6);
        info.setContent("欢迎注册 签签世界 ，本次验证码");
        info.setContent(code + " 是您的验证码，注意验证码有效期为15分钟哦！");
        info.setReceiver(email);
        if (Constant.isDev) {
            code = 123456;
        } else {
            MailUtil.sendTextMail(info);
        }
        RedisUtil.set(Constant.authCodeKey(email), String.valueOf(code), Constant.AUTH_CODE_EXPIRE);
    }

    /**
     * Description: 用户注册
     *
     * @param body 注册表单
     * @author fanxb
     * @date 2019/7/6 11:30
     */
    public void register(RegisterBody body) {
        String codeKey = Constant.authCodeKey(body.getEmail());
        String realCode = RedisUtil.get(codeKey, String.class);
        if ((!StringUtil.isEmpty(realCode)) && (!realCode.equals(body.getAuthCode()))) {
            throw new CustomException("验证码错误");
        }
        RedisUtil.delete(codeKey);
        User user = new User();
        user.setUsername(body.getUsername());
        user.setEmail(body.getEmail());
        user.setIcon(DEFAULT_ICON);
        user.setPassword(HashUtil.sha1(HashUtil.md5(body.getPassword())));
        user.setCreateTime(System.currentTimeMillis());
        user.setLastLoginTime(0);
        userDao.addOne(user);
    }

    /**
     * Description: 登录
     *
     * @param body 登录表单
     * @return LoginRes
     * @author fanxb
     * @date 2019/7/6 16:37
     */
    public LoginRes login(LoginBody body) {
        User userInfo = userDao.selectByUsernameOrEmail(body.getStr());
        if (userInfo == null) {
            throw new FormDataException("账号/密码错误");
        }
        if (!HashUtil.sha1(HashUtil.md5(body.getPassword())).equals(userInfo.getPassword())) {
            throw new FormDataException("账号/密码错误");
        }
        Map<String, String> data = new HashMap<>(1);
        data.put("userId", String.valueOf(userInfo.getUserId()));
        String token = JwtUtil.encode(data, Constant.jwtSecret, body.isRememberMe() ? LONG_EXPIRE_TIME : SHORT_EXPIRE_TIME);
        LoginRes res = new LoginRes();
        res.setToken(token);
        res.setUserId(userInfo.getUserId());
        res.setUsername(userInfo.getUsername());
        res.setEmail(userInfo.getEmail());
        res.setIcon(userInfo.getIcon());
        userDao.updateLastLoginTime(System.currentTimeMillis(), userInfo.getUserId());
        return res;
    }
}
