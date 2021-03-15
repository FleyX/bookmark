package com.fanxb.bookmark.business.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.fanxb.bookmark.business.user.constant.RedisConstant;
import com.fanxb.bookmark.business.user.dao.UserDao;
import com.fanxb.bookmark.business.user.service.BaseInfoService;
import com.fanxb.bookmark.business.user.vo.EmailUpdateBody;
import com.fanxb.bookmark.business.user.vo.UpdatePasswordBody;
import com.fanxb.bookmark.common.constant.Constant;
import com.fanxb.bookmark.common.entity.MailInfo;
import com.fanxb.bookmark.common.entity.User;
import com.fanxb.bookmark.common.exception.CustomException;
import com.fanxb.bookmark.common.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/9/18 15:54
 */
@Service
@Slf4j
public class BaseInfoServiceImpl implements BaseInfoService {

    private static final String VERIFY_EMAIL = FileUtil.streamToString(BaseInfoServiceImpl.class
            .getClassLoader().getResourceAsStream("verifyEmail.html"));

    private static final String VERIFY_EMAIL_PATH = "/public/verifyEmail?key=";

    private final UserDao userDao;

    @Autowired
    public BaseInfoServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void changePassword(UpdatePasswordBody body) {
        int userId = UserContextHolder.get().getUserId();
        String password = userDao.selectByUserIdOrGithubId(userId, null).getPassword();
        if (StrUtil.isNotEmpty(password) && !StrUtil.equals(password, HashUtil.getPassword(body.getOldPassword()))) {
            throw new CustomException("旧密码错误");
        }
        userDao.updatePasswordByUserId(userId, HashUtil.getPassword(body.getPassword()));
    }


    @Override
    public void updateUsername(String username) {
        userDao.updateUsernameByUserId(UserContextHolder.get().getUserId(), username);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmail(EmailUpdateBody body) {
        int userId = UserContextHolder.get().getUserId();
        String oldPassword = userDao.selectByUserIdOrGithubId(userId, null).getPassword();
        if (!StrUtil.equals(oldPassword, HashUtil.getPassword(body.getOldPassword()))) {
            throw new CustomException("密码校验失败，无法更新email");
        }
        String secret = UUID.randomUUID().toString().replaceAll("-", "");
        String url = VERIFY_EMAIL.replaceAll("XXXX", Constant.serviceAddress + VERIFY_EMAIL_PATH + secret);
        log.debug(url);
        MailInfo info = new MailInfo(body.getEmail(), "验证邮箱", url);
        MailUtil.sendMail(info, true);
        RedisUtil.set(RedisConstant.getUpdateEmailKey(secret), String.valueOf(userId), TimeUtil.DAY_MS);
        userDao.updateNewEmailByUserId(userId, body.getEmail());
    }

    @Override
    public void verifyEmail(String secret) {
        String key = RedisConstant.getUpdateEmailKey(secret);
        Integer userId = RedisUtil.get(key, Integer.class);
        RedisUtil.delete(key);
        if (userId == null) {
            throw new CustomException("校验失败,请重试");
        }
        userDao.updateEmailByUserId(userId);
    }

    @Override
    public void changeDefaultSearchEngine(User user) {
        userDao.updateSearchEngine(user.getUserId(), user.getDefaultSearchEngine());
    }
}
