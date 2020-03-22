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

    @Value("${spring.profiles.active}")
    public void setIsDev(String active) {
        Constant.isDev = active.contains("dev");
    }

    public static String jwtSecret = "";

    @Value("${jwtSecret}")
    public void setJwtSecret(String jwtSecret) {
        Constant.jwtSecret = jwtSecret;
    }

    /**
     * 文件存储基路径
     */
    public static String fileSavePath = "./";

    @Value("${fileSavePath}")
    public void setFileSavePath(String path) {
        fileSavePath = path;
    }


    /**
     * 服务部署地址
     */
    public static String serviceAddress = "http://localhost";

    @Value("${serviceAddress}")
    public void setServiceAddress(String address) {
        serviceAddress = address;
    }

    /**
     * 拼音服务调用地址
     */
    public static String pinyinBaseUrl;
    @Value("${pinyin.base-url}")
    public void setPinyinBaseUrl(String baseUrl){
        pinyinBaseUrl=baseUrl;
    }

    /**
     * 调用拼音服务token
     */
    public static String pinyinToken;
    @Value("${pinyin.token}")
    public void setPinyinToken(String pinyinToken) {
        Constant.pinyinToken = pinyinToken;
    }
}
