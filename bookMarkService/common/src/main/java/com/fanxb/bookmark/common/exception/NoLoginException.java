package com.fanxb.bookmark.common.exception;

/**
 * 类功能简述： 未登录异常，使用-1表示
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/5 15:49
 */
public class NoLoginException extends CustomException {
    NoLoginException() {
        super("您尚未登录", -1, null);
    }
}
