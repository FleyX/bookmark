package com.fanxb.bookmark.business.user.vo;

import lombok.Data;

/**
 * 第三方登陆入参
 *
 * @author fanxb
 * @date 2021/3/10
 **/
@Data
public class OAuthBody {
    public static final String GITHUB = "github";
    /**
     * 类别
     */
    private String type;
    /**
     * 识别码
     */
    private String code;
    /**
     * 是否保持登陆
     */
    private boolean rememberMe;
}
