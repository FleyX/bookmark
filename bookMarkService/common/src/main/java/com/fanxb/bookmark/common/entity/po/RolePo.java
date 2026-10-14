package com.fanxb.bookmark.common.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 类功能简述：角色表实体
 *
 * @author fanxb
 */
@Data
@TableName("role")
public class RolePo {

    @TableId(type = IdType.AUTO)
    private Integer roleId;

    /**
     * 角色编码，唯一
     */
    private String code;

    private String name;

    private String description;

    /**
     * 1启用 0禁用
     */
    private Integer status;

    /**
     * 1内置角色(不可改删)
     */
    private Integer buildIn;

    private Long createTime;

    private Long updateTime;
}
