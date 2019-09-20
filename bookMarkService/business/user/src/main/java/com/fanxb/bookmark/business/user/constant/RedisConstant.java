package com.fanxb.bookmark.business.user.constant;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/9/20 16:44
 */
public class RedisConstant {

    /**
     * 修改邮箱验证有效期
     */
    public static final int UPDATE_EMAIL_EXPIRE = 120;

    /**
     * 获取redis key
     * @param code code
     */
    public static String getUpdateEmailKey(String code) {
        return "update_email_" + code;
    }
}
