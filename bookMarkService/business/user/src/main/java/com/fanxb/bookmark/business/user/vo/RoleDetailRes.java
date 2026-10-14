package com.fanxb.bookmark.business.user.vo;

import lombok.Data;

import java.util.List;

/**
 * 类功能简述：角色详情响应，含已绑权限code
 *
 * @author fanxb
 */
@Data
public class RoleDetailRes {

    private Integer roleId;

    private String code;

    private String name;

    private String description;

    /**
     * 1启用 0禁用
     */
    private Integer status;

    /**
     * 1内置角色
     */
    private Integer buildIn;

    private Long createTime;

    private Long updateTime;

    private List<String> permissionCodes;
}
