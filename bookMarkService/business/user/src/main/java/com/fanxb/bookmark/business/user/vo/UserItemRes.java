package com.fanxb.bookmark.business.user.vo;

import lombok.Data;

/**
 * 类功能简述：用户管理列表项
 *
 * @author fanxb
 */
@Data
public class UserItemRes {

    private Integer userId;

    private String username;

    private String email;

    private String icon;

    private Long createTime;

    private Long lastLoginTime;

    /**
     * 1正常 0禁用
     */
    private Integer status;
}
