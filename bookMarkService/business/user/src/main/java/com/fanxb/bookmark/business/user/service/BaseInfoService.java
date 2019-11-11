package com.fanxb.bookmark.business.user.service;

import com.fanxb.bookmark.business.user.constant.RedisConstant;
import com.fanxb.bookmark.business.user.dao.UserDao;
import com.fanxb.bookmark.business.user.entity.EmailUpdateBody;
import com.fanxb.bookmark.business.user.entity.UpdatePasswordBody;
import com.fanxb.bookmark.common.constant.Constant;
import com.fanxb.bookmark.common.entity.MailInfo;
import com.fanxb.bookmark.common.exception.CustomException;
import com.fanxb.bookmark.common.exception.FormDataException;
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
public class BaseInfoService {

    private static final String VERIFY_EMAIL = FileUtil.streamToString(BaseInfoService.class
            .getClassLoader().getResourceAsStream("verifyEmail.html"));

    private static final String VERIFY_EMAIL_PATH = "/public/verifyEmail?key=";

    @Autowired
    private UserDao userDao;

    public void changePassword(UpdatePasswordBody body) {
        int userId = UserContextHolder.get().getUserId();
        String realOldPass = userDao.selectByUserId(userId).getPassword();
        if (!realOldPass.equals(HashUtil.getPassword(body.getOldPass()))) {
            throw new FormDataException("旧密码错误");
        }
        userDao.updatePasswordByUserId(userId, HashUtil.getPassword(body.getNewPass()));
    }


    /**
     * Description: 修改用户名
     *
     * @param username 用户名
     * @author fanxb
     * @date 2019/9/20 16:18
     */
    public void updateUsername(String username) {
        userDao.updateUsernameByUserId(UserContextHolder.get().getUserId(), username);
    }

    /**
     * 功能描述: 预备更新email，需要校验密码
     *
     * @param body body
     * @author fanxb
     * @date 2019/9/26 17:27
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateEmail(EmailUpdateBody body) {
        int userId = UserContextHolder.get().getUserId();
        String checkAuthKey = com.fanxb.bookmark.common.constant.RedisConstant.getPasswordCheckKey(userId, body.getActionId());
        String str = RedisUtil.get(checkAuthKey, String.class);
        if (str == null) {
            throw new CustomException("密码校验失败，无法更新email");
        }
        RedisUtil.delete(checkAuthKey);
        String secret = UUID.randomUUID().toString().replaceAll("-", "");
        String url = VERIFY_EMAIL.replaceAll("XXXX", Constant.serviceAddress + VERIFY_EMAIL_PATH + secret);
        log.debug(url);
        MailInfo info = new MailInfo(body.getNewEmail(), "验证邮箱", url);
        MailUtil.sendMail(info, true);
        RedisUtil.set(RedisConstant.getUpdateEmailKey(secret), String.valueOf(userId), TimeUtil.DAY_MS);
        userDao.updateNewEmailByUserId(userId, body.getNewEmail());
    }

    /**
     * 功能描述: 校验新邮箱，校验成功就更新
     *
     * @param secret secret
     * @author fanxb
     * @date 2019/11/11 23:24
     */
    public void verifyEmail(String secret) {
        String key = RedisConstant.getUpdateEmailKey(secret);
        Integer userId = RedisUtil.get(key, Integer.class);
        RedisUtil.delete(key);
        if (userId == null) {
            throw new CustomException("校验失败,请重试");
        }
        userDao.updateEmailByUserId(userId);
    }
}
