package com.fanxb.bookmark.common.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/26 11:31
 */
@Slf4j
public class EsException extends CustomException {

    public EsException(Exception e) {
        super("当前服务不可用，请稍后重试", 0, e);
    }
}
