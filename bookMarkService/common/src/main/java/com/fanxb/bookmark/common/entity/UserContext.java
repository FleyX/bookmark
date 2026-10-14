package com.fanxb.bookmark.common.entity;

import lombok.Data;

import java.util.Date;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/3/1 14:26
 */
@Data
public class UserContext {

    private int userId;
    private String jwt;
    /**
     * jwt 签发时间，旧 token 无该声明时为 null
     */
    private Date jwtIssuedAt;
}
