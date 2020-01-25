package com.fanxb.bookmark.common.constant;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2019/11/11
 * Time: 23:01
 */
public class RedisConstant {
    public static String getPasswordCheckKey(int userId, String actionId) {
        return "password_check_key_" + userId + "_" + actionId;
    }

    /**
     * 某用户书签数据更新时间,该队列左进右出
     */
    public static final String BOOKMARK_UPDATE_TIME = "bookmark_update_time";
}
