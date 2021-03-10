package com.fanxb.bookmark.business.user.vo;

import lombok.Data;

/**
 * 类功能简述：登录表单
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/6 17:25
 */
@Data
public class LoginBody {
    /**
     * 用户名或邮箱
     */
    private String str;
    private String password;
    private boolean rememberMe;
}
