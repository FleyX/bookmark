package com.fanxb.bookmark.business.user.dao;

import com.fanxb.bookmark.common.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/6 11:36
 */
@Component
public interface UserDao {

    /**
     * Description: 新增一个用户
     *
     * @param user user
     * @author fanxb
     * @date 2019/7/6 11:37
     */
    void addOne(User user);

    /**
     * Description: 通过用户名或者email获取用户信息
     *
     * @param name  username
     * @param email email
     * @return com.fanxb.bookmark.common.entity.User
     * @author fanxb
     * @date 2019/7/6 16:45
     */
    User selectByUsernameOrEmail(@Param("name") String name, @Param("email") String email);

    /**
     * Description: 更新用户上次登录时间
     *
     * @param time   时间
     * @param userId 用户id
     * @author fanxb
     * @date 2019/7/6 16:46
     */
    void updateLastLoginTime(@Param("time") long time, @Param("userId") int userId);

    /**
     * Description: 更新一个参数
     *
     * @param password 新密码
     * @param email  邮箱
     * @author fanxb
     * @date 2019/7/9 20:03
     */
    void resetPassword(@Param("password") String password,@Param("email") String email);

    /**
     * Description: 根据用户id查询用户信息
     *
     * @author fanxb
     * @date 2019/7/30 16:08
     * @param userId userId
     * @return com.fanxb.bookmark.common.entity.User
     */
    User selectByUserId(int userId);
}
