package com.fanxb.bookmark.common.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/4 20:14
 */
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Integer userId;
    /**
     * 第三方github登陆id,-1说明非github登陆
     */
    private Long githubId;
    private String username;
    private String email;
    private String newEmail;
    private String icon;
    /**
     * 是否未设置密码
     */
    @TableField(exist = false)
    private Boolean noPassword;
    @JsonProperty(access =  JsonProperty.Access.READ_ONLY)
    private String password;
    private long createTime;
    /**
     * 上次登录时间
     */
    private long lastLoginTime;
    /**
     * 书签同步版本
     */
    private int version;

    /**
     * 1正常 0禁用
     */
    private Integer status;

    private LocalDateTime lastActiveTime;

    /**
     * 当前用户已展开的资源code列表，仅currentUserInfo接口返回，非数据库字段
     */
    @TableField(exist = false)
    private List<String> resources;

}
