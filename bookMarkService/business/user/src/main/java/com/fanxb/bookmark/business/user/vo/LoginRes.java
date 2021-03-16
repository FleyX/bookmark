package com.fanxb.bookmark.business.user.vo;

import com.fanxb.bookmark.common.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类功能简述：登录返回数据
 *
 * @author fanxb
 * @date 2019/7/6 16:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRes {
    /**
     * 用户信息
     */
    private User user;
    /**
     * token
     */
    private String token;
}
