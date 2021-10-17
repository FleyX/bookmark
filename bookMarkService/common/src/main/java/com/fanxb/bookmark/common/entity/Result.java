package com.fanxb.bookmark.common.entity;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/3/19 18:05
 */
public class Result {

    /**
     * 状态，1：正常，0：异常，-1：未认证
     */
    private int code;
    private String message;
    private Object data;

    public Result() {
    }

    public Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result unAuth() {
        return new Result(-1, "", null);
    }

    public static Result success() {
        return new Result(1, null, null);
    }

    public static Result success(Object data) {
        return new Result(1, null, data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
