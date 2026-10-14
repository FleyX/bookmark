package com.fanxb.bookmark.business.user.vo;

import lombok.Data;

/**
 * 类功能简述：用户绑定的角色精简信息
 *
 * @author fanxb
 */
@Data
public class UserRoleRes {

    private Integer roleId;

    private String code;

    private String name;
}
