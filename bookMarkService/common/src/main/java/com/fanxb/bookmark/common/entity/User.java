package com.fanxb.bookmark.common.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
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
public class User {

    private int userId;
    private String username;
    private String email;
    private String newEmail;
    private String icon;
    @JSONField(serialize = false)
    private String password;
    private long createTime;
    private long lastLoginTime;
    /**
     * 书签同步版本
     */
    private int version;
}
