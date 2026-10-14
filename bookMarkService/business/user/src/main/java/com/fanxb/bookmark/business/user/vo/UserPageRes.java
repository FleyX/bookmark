package com.fanxb.bookmark.business.user.vo;

import lombok.Data;

import java.util.List;

/**
 * 类功能简述：用户分页响应
 *
 * @author fanxb
 */
@Data
public class UserPageRes {

    private final long total;

    private final List<UserItemRes> list;
}
