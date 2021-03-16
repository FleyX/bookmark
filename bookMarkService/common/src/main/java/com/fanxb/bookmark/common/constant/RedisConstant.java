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
    public static final String BOOKMARK_UPDATE_VERSION = "bookmark_update_version";
    /**
     * 某个用户上传了文件夹，需要进行书签转化
     */
    public static final String BOOKMARK_PINYIN_CHANGE = "bookmark_pinyin_change";
    /**
     * 插入书签数据到es中
     */
    public static final String BOOKMARK_INSERT_ES = "bookmark_insert_es";
    /**
     * 从es中删除数据
     */
    public static final String BOOKMARK_DELETE_ES = "bookmark_DELETE_es";
    /**
     * 书签访问次数+1
     */
    public static final String BOOKMARK_VISIT_NUM_PLUS = "bookmark_visit_num_plus";

    public static String getUserFailCountKey(String username) {
        return "bookmark_user_fail_count_" + username;
    }
}
