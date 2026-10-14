package com.fanxb.bookmark.business.user.vo;

import lombok.Data;

/**
 * 类功能简述：角色精简选项，供分配角色下拉使用
 *
 * @author fanxb
 */
@Data
public class RoleOptionRes {

    private Integer roleId;

    private String code;

    private String name;
}
