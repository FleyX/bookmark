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
     * @param name username
     * @param email email
     * @return com.fanxb.bookmark.common.entity.User
     * @author fanxb
     * @date 2019/7/6 16:45
     */
    User selectByUsernameOrEmail(@Param("name") String name,@Param("email") String email);

    /**
     * Description: 更新用户上次登录时间
     *
     * @param time   时间
     * @param userId 用户id
     * @author fanxb
     * @date 2019/7/6 16:46
     */
    void updateLastLoginTime(@Param("time") long time, @Param("userId") int userId);
}
