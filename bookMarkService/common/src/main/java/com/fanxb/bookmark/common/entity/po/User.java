package com.fanxb.bookmark.common.entity.po;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

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
    @JSONField(serialize = false)
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
     * 默认搜索引擎
     */
    private Integer searchEngineId;

}
