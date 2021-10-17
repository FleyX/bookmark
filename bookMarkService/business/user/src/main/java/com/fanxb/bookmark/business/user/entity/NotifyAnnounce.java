package com.fanxb.bookmark.business.user.entity;

/**
 * @author fanxb
 * @date 2021-10-17 14:04
 */
public class NotifyAnnounce {
    /**
     * 通知id
     */
    private int notifyAnnounceId;
    /**
     * 发送人id
     */
    private int senderId;
    /**
     * 标题
     */
    private String title;
    /**
     * 正文
     */
    private String content;
    /**
     * 创建时间
     */
    private Long createdDate;
    /**
     * 通知开始时间
     */
    private Long startDate;
    /**
     * 通知结束时间
     */
    private Long endDate;
}
