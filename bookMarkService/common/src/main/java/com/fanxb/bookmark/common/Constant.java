package com.fanxb.bookmark.common;

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
    public static int VERIFY_CODE_EXPIRE = 15 * 60 * 1000;


}
