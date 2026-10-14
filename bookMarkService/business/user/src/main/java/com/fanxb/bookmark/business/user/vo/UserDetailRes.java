package com.fanxb.bookmark.business.user.vo;

import lombok.Data;

import java.util.List;

/**
 * 类功能简述：用户详情响应，含书签数与已绑角色
 *
 * @author fanxb
 */
@Data
public class UserDetailRes {

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

    /**
     * 书签数量，不含文件夹
     */
    private long bookmarkCount;

    private List<UserRoleRes> roles;
}
