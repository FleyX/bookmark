package com.fanxb.bookmark.business.user.service;

import com.alibaba.fastjson.JSON;
import com.fanxb.bookmark.business.user.constant.RedisConstant;
import com.fanxb.bookmark.business.user.dao.UserDao;
import com.fanxb.bookmark.business.user.entity.EmailUpdateBody;
import com.fanxb.bookmark.business.user.entity.EmailUpdateRedis;
import com.fanxb.bookmark.business.user.entity.UpdatePasswordBody;
import com.fanxb.bookmark.common.constant.Constant;
import com.fanxb.bookmark.common.entity.MailInfo;
import com.fanxb.bookmark.common.exception.FormDataException;
import com.fanxb.bookmark.common.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
     * 功能描述: 预备更新email，需要进行校验
     *
     * @param body body
     * @author fanxb
     * @date 2019/9/26 17:27
     */
    public void updateEmail(EmailUpdateBody body) {
        int userId = UserContextHolder.get().getUserId();
        String realPass = userDao.selectByUserId(userId).getPassword();
        if (!realPass.equals(HashUtil.getPassword(body.getOldPass()))) {
            throw new FormDataException("旧密码错误");
        }
        String key = UUID.randomUUID().toString().replaceAll("-", "");
        String url = VERIFY_EMAIL.replaceAll("XXXX", Constant.serviceAddress + VERIFY_EMAIL_PATH + key);
        log.debug(url);
        MailInfo info = new MailInfo(body.getNewEmail(), "验证邮箱", url);
        MailUtil.sendMail(info, true);
        EmailUpdateRedis redisBody = new EmailUpdateRedis(userId, body.getNewEmail());
        RedisUtil.set(RedisConstant.getUpdateEmailKey(key), JSON.toJSONString(redisBody), TimeUtil.DAY_MS);
    }

    public void verifyEmail(){

    }
}
