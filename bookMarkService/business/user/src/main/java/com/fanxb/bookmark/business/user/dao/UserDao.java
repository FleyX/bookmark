package com.fanxb.bookmark.business.user.dao;

import com.fanxb.bookmark.common.entity.User;
import com.fanxb.bookmark.common.entity.redis.UserBookmarkUpdate;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

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
     * @param email    邮箱
     * @author fanxb
     * @date 2019/7/9 20:03
     */
    void resetPassword(@Param("password") String password, @Param("email") String email);

    /**
     * Description: 根据用户id查询用户信息
     *
     * @param userId   userId
     * @param githubId githubId
     * @return com.fanxb.bookmark.common.entity.User
     * @author fanxb
     * @date 2019/7/30 16:08
     */
    User selectByUserIdOrGithubId(@Param("userId") Integer userId, @Param("githubId") Long githubId);

    /**
     * Description: 更新用户icon
     *
     * @param userId userId
     * @param icon   icon
     * @author fanxb
     * @date 2019/9/9 13:57
     */
    @Update("update user set icon=#{icon} where userId=#{userId}")
    void updateUserIcon(@Param("userId") int userId, @Param("icon") String icon);

    /**
     * Description: 根据用户id修改密码
     *
     * @param userId  userId
     * @param newPass newPass
     * @author fanxb
     * @date 2019/9/20 14:39
     */
    @Update("update user set password=#{password} where userId=#{userId}")
    void updatePasswordByUserId(@Param("userId") int userId, @Param("password") String newPass);

    /**
     * Description: 根据用户id修改用户名
     *
     * @param userId   userId
     * @param username username
     * @author fanxb
     * @date 2019/9/20 16:22
     */
    @Update("update user set username=#{username} where userId=#{userId}")
    void updateUsernameByUserId(@Param("userId") int userId, @Param("username") String username);

    /**
     * 更新用户新邮箱
     *
     * @param userId   userId
     * @param newEmail email
     */
    @Update("update user set newEmail=#{newEmail} where userId= #{userId}")
    void updateNewEmailByUserId(@Param("userId") int userId, @Param("newEmail") String newEmail);

    /**
     * 新邮箱校验成功，更新邮箱
     *
     * @param userId userId
     */
    @Update("update user set email=newEmail,newEmail='' where userId=#{userId}")
    void updateEmailByUserId(int userId);

    /**
     * 功能描述: 更新用户上次更新书签时间
     *
     * @param userId userId
     * @author fanxb
     * @date 2020/1/26 下午3:47
     */
    @Update("update user set version=version+1 where userId=#{userId}")
    void updateUserVersion(int userId);

    /**
     * 功能描述: 更新所有用户的更新时间
     *
     * @author fanxb
     * @date 2020/3/29 18:18
     */
    @Update("update user set version=version+1")
    void updateAllBookmarkUpdateVersion();

    /**
     * 判断用户名是否存在
     *
     * @param name name
     * @return boolean
     * @author fanxb
     * @date 2021/3/11
     **/
    @Select("select count(1) from user where username=#{name}")
    boolean usernameExist(String name);

    /**
     * 更新githubId
     *
     * @param user user
     * @author fanxb
     * @date 2021/3/11
     **/
    @Update("update user set githubId=#{githubId},email=#{email} where userId=#{userId}")
    void updateEmailAndGithubId(User user);

    /**
     * 获取用户版本
     *
     * @param userId userId
     * @return int
     * @author fanxb
     * @date 2021/3/11
     **/
    @Select("select version from user where userId=#{userId}")
    int getUserVersion(int userId);

    /**
     * 分页查询用户id列表
     *
     * @param start 开始
     * @param size  页大小
     * @return java.util.List<java.lang.Integer>
     * @author fanxb
     * @date 2021/3/13
     **/
    @Select("select userId from user order by userId limit #{start},#{size}")
    List<Integer> selectUserIdPage(@Param("start") int start, @Param("size") int size);

    /**
     * 更新用户搜索引擎
     *
     * @param userId userId
     * @param engine engine
     * @author fanxb
     * @date 2021/3/14
     **/
    @Update("update user set defaultSearchEngine=#{engine} where userId=#{userId}")
    void updateSearchEngine(@Param("userId") int userId, @Param("engine") String engine);
}
