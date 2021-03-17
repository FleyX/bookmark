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
    long LONG_EXPIRE_TIME = 30L * TimeUtil.DAY_MS;

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
     * 获取当前用户的version
     *
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
     * @author fanxb
     * @date 2021/3/17
     **/
    Map<Integer, Set<String>> dealAllUserBookmark(boolean delete);
}
