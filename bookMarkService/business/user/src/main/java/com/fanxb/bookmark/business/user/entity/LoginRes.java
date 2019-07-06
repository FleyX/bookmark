package com.fanxb.bookmark.business.user.entity;

import lombok.Data;

/**
 * 类功能简述：登录返回数据
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/6 16:52
 */
@Data
public class LoginRes {
    private String token;
    private int userId;
    private String username;
    private String email;
    private String lastLoginTime;
    private String icon;
}
