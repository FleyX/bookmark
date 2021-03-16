package com.fanxb.bookmark.common.entity;

import lombok.Data;

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
    /**
     * 是否管理员用户
     */
    private Boolean manageUser;
    private String jwt;
}
