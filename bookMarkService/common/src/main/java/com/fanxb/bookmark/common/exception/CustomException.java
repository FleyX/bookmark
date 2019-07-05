package com.fanxb.bookmark.common.exception;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/3/19 18:09
 */
public class CustomException extends RuntimeException {

    String message;

    public CustomException() {
        super();
    }

    public CustomException(String message) {
        super(message);
        this.message = message;
    }

    public CustomException(Exception e){
        super(e);
    }

    public CustomException(String message, Exception e) {
        super(e);
        this.message = message;
    }

    @Override
    public String getMessage() {
        if (this.message == null) {
            return super.getMessage();
        } else {
            return this.message;
        }
    }
}
