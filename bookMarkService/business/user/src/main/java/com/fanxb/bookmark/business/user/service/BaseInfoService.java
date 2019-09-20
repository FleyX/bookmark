package com.fanxb.bookmark.business.user.service;

import com.fanxb.bookmark.business.user.dao.UserDao;
import com.fanxb.bookmark.business.user.entity.EmailUpdateBody;
import com.fanxb.bookmark.business.user.entity.UpdatePasswordBody;
import com.fanxb.bookmark.common.constant.Constant;
import com.fanxb.bookmark.common.entity.MailInfo;
import com.fanxb.bookmark.common.exception.FormDataException;
import com.fanxb.bookmark.common.util.HashUtil;
import com.fanxb.bookmark.common.util.MailUtil;
import com.fanxb.bookmark.common.util.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/9/18 15:54
 */
@Service
public class BaseInfoService {

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

    public void updateEmail(EmailUpdateBody body){
        int userId = UserContextHolder.get().getUserId();
        String realPass=userDao.selectByUserId(userId).getPassword();
        if (!realPass.equals(HashUtil.getPassword(body.getOldPass()))) {
            throw new FormDataException("旧密码错误");
        }
        MailInfo info = new MailInfo(body.getNewEmail(),"验证邮箱",);
        MailUtil.sendTextMail();
        Constant

    }
}
