package com.fanxb.bookmark.business.user.service;

import com.fanxb.bookmark.common.util.TimeUtil;

/**
 * 用户接口
 *
 * @author fanxb
 * @date 2021/3/11
 **/
public interface UserService {
    static final String DEFAULT_ICON = "/favicon.ico";
    /**
     * 短期jwt失效时间
     */
    static final long SHORT_EXPIRE_TIME = 2 * 60 * 60 * 1000;
    /**
     * 长期jwt失效时间
     */
    static final long LONG_EXPIRE_TIME = 30L * TimeUtil.DAY_MS;

    /**
     * 头像文件大小限制 单位：KB
     */
    static final int ICON_SIZE = 200;

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
}
