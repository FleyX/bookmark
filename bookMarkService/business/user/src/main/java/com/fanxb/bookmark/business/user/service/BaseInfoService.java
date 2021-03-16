package com.fanxb.bookmark.business.user.service;

import com.fanxb.bookmark.business.user.vo.EmailUpdateBody;
import com.fanxb.bookmark.business.user.vo.UpdatePasswordBody;
import com.fanxb.bookmark.common.entity.User;

/**
 * 个人信息修改
 *
 * @author fanxb
 * @date 2021/3/14
 **/
public interface BaseInfoService {

    /**
     * 修改密码
     *
     * @param body body
     * @author fanxb
     * @date 2021/3/14
     **/
    void changePassword(UpdatePasswordBody body);

    /**
     * Description: 修改用户名
     *
     * @param username 用户名
     * @author fanxb
     * @date 2019/9/20 16:18
     */
    void updateUsername(String username);

    /**
     * 功能描述: 预备更新email，需要校验密码
     *
     * @param body body
     * @author fanxb
     * @date 2019/9/26 17:27
     */
    void updateEmail(EmailUpdateBody body);

    /**
     * 功能描述: 校验新邮箱，校验成功就更新
     *
     * @param secret secret
     * @author fanxb
     * @date 2019/11/11 23:24
     */
    void verifyEmail(String secret);

    /**
     * 修改用户默认搜索引擎
     *
     * @param user user
     * @author fanxb
     * @date 2021/3/14
     **/
    void changeDefaultSearchEngine(User user);
}
