package com.fanxb.bookmark.business.user.vo;

import lombok.Data;

/**
 * 类功能简述： 注册表单
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/6 11:23
 */
@Data
public class RegisterBody {
    private String username;
    private String password;
    private String email;
    private String authCode;
}
