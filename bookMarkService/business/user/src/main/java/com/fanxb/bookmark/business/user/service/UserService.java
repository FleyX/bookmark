package com.fanxb.bookmark.business.user.service;

import com.fanxb.bookmark.common.util.TimeUtil;

import java.util.Map;
import java.util.Set;

/**
 * 用户接口
 *
 * @author fanxb
 * @date 2021/3/11
 **/
public interface UserService {
    String DEFAULT_ICON = "/favicon.ico";
    /**
     * 短期jwt失效时间
     */
    long SHORT_EXPIRE_TIME = 2 * 60 * 60 * 1000;
    /**
     * 长期jwt失效时间
     */
    long LONG_EXPIRE_TIME = 300L * TimeUtil.DAY_MS;

    /**
     * 头像文件大小限制 单位：KB
     */
    int ICON_SIZE = 200;

    /***
     * 获取一个可用的用户名
     * @author fanxb
     * @return java.lang.String
     * @date 2021/3/11
     **/
    String createNewUsername();

    /**
     * 用户表为空时创建初始化管理员 admin，绑定超级管理员并初始化默认数据
     *
     * @return 非dev环境创建成功时返回本次明文随机密码；dev环境创建成功或用户表非空时返回null
     * @author fanxb
     */
    String initializeAdmin();

    /**
     * 若系统尚无超级管理员绑定，则将指定用户绑定为超级管理员（注册/首次OAuth登录时调用，须在事务内）
     *
     * @param userId 用户id
     * @author fanxb
     */
    void bindSuperAdminIfAbsent(int userId);

    /**
     * 获取当前用户的version
     *
     * @param userId userId
     * @return int
     * @author fanxb
     * @date 2021/3/11
     **/
    int getCurrentUserVersion(int userId);

    /**
     * 更新所有用户的空icon
     *
     * @author fanxb
     * @date 2021/3/13
     **/
    void updateAllUserIcon();

    /**
     * 检查所有用户的问题书签数据
     *
     * @param delete 是否删除问题数据
     * @return 返回用户删除的数据
     * @author fanxb
     * @date 2021/3/17
     **/
    Map<Integer, Set<String>> dealAllUserBookmark(boolean delete);
}
