package com.fanxb.bookmark.business.user.vo;

import lombok.Data;

/**
 * @author fanxb
 * @date 2021-10-17 14:22
 */
@Data
public class UserNotifyAnnounceRes {
    private int notifyAnnounceId;
    private String title;
    private String content;
    private long readDate;
}
