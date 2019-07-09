package com.fanxb.bookmark.common.entity;

import lombok.Data;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/9 14:47
 */
@Data
public class Url {
    private int userId;
    private String method;
    private String url;
    private int type;
}
