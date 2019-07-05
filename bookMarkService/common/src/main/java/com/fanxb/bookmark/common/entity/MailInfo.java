package com.fanxb.bookmark.common.entity;

import lombok.Data;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2019/4/7
 * Time: 18:41
 */
@Data
public class MailInfo {
    private String receiver;
    private String subject;
    private String content;

    public MailInfo() {
    }

    public MailInfo(String receiver, String subject, String content) {
        this.receiver = receiver;
        this.subject = subject;
        this.content = content;
    }
}
