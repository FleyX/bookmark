package com.fanxb.bookmark.common.entity;

import com.alibaba.fastjson.JSON;
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
    @JsonIgnore
    private String password;
    private long createTime;
    private long lastLoginTime;
    /**
     * 上次更新书签时间
     */
    private long bookmarkChangeTime;

    public static void main(String[] args) {
        User user1 = new User();
        User user2 = new User();
        Map<User, User> map = new HashMap<>();
        map.put(user1, user2);
        NewUserObj obj = new NewUserObj();
        obj.setMap(map);
        NewUserObj newObj = JSON.parseObject(JSON.toJSONString(obj), NewUserObj.class);
        System.out.println(newObj.getMap().get(user1));
    }

    @Data
    public static class NewUserObj {
        private Map<User, User> map;
    }
}
