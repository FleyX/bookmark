package com.fanxb.bookmark.common.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 类功能简述：系统及常量类
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/4/4 16:10
 */
@Component
public class Constant {


    /**
     * jwt key
     */
    public static final String JWT_KEY = "jwt-token";

    /**
     * jwt 过期时间，ms
     */
    public static int JWT_EXPIRE_TIME = 60 * 60 * 60 * 1000;

    /**
     * 验证码过期时间
     */
    public static int AUTH_CODE_EXPIRE = 15 * 60 * 1000;

    /**
     * Description: 生成email存在redis中的key
     *
     * @param email 邮箱地址
     * @return java.lang.String
     * @author fanxb
     * @date 2019/7/6 10:56
     */
    public static String authCodeKey(String email) {
        return email + "_authCode";
    }

    public static boolean isDev = false;

    @Value("${isDev}")
    public void setIsDev(boolean isDev) {
        Constant.isDev = isDev;
    }

    public static String jwtSecret = "";

    @Value("${jwtSecret}")
    public void setJwtSecret(String jwtSecret) {
        Constant.jwtSecret = jwtSecret;
    }
}
