package com.fanxb.bookmark.common.exception;

import com.fanxb.bookmark.common.util.StringUtil;

/**
 * 类功能简述： 自定义错误类，默认错误码为0,表示一般错误
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/3/19 18:09
 */
public class CustomException extends RuntimeException {

    private String message;
    private int code;

    public CustomException() {
        this(null, null, null);
    }

    public CustomException(String message) {
        this(message, null, null);
    }

    public CustomException(Exception e) {
        this(null, null, e);
    }

    public CustomException(String message, Exception e) {
        this(message, null, e);
    }

    public CustomException(String message, Integer code, Exception e) {
        super(e);
        this.message = message == null ? "" : message;
        this.code = code == null ? 0 : code;
    }

    @Override
    public String getMessage() {
        if (StringUtil.isEmpty(this.message)) {
            return super.getMessage();
        } else {
            return this.message;
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
