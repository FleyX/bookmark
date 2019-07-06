package com.fanxb.bookmark.common.exception;

/**
 * 类功能简述：表单错误引起的异常,使用-2表示
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/6 17:08
 */
public class FormDataException extends CustomException {
    public FormDataException(String message) {
        super(message, -2, null);
    }
}
