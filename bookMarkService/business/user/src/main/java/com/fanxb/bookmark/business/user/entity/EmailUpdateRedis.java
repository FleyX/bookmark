package com.fanxb.bookmark.business.user.entity;

import lombok.Data;

/**
 * 类功能简述：记录用户id和要修改的邮件地址
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/9/20 16:47
 */
@Data
public class EmailUpdateRedis {
    private int userId;
    private String email;

    public EmailUpdateRedis() {
    }

    public EmailUpdateRedis(int userId, String email) {
        this.userId = userId;
        this.email = email;
    }
}
