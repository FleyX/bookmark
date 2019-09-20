package com.fanxb.bookmark.business.user.constant;

/**
 * 类功能简述：校验相关常量
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/9/20 14:49
 */
public interface ValidatedConstant {

    /**
     * 密码正则
     */
    String PASSWORD_REG = "^(?=.*?\\d+.*?)(?=.*?[a-zA-Z]+.*?)[\\da-zA-Z]{6,20}$";
    /**
     * 密码校验错误提示语
     */
    String PASSWORD_MESSAGE = "密码为6-20位数字和字母组合";

    /**
     * 用户名正则
     */
    String USERNAME_REG = "[\\da-zA-Z]{1,10}";
    /**
     * 用户名错误提示语
     */
    String USERNAME_MESSAGE = "用户名为1-10位的数字或字母组合";
}
