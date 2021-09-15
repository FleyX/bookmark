package com.fanxb.bookmark.business.user.entity;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * Created with IntelliJ IDEA
 *
 * @author fanxb
 * Date: 2020/3/10
 * Time: 23:13
 */
@Data
public class Feedback {
    private int feedbackId;
    private int userId;
    private String type;
    @NotEmpty(message = "内容不能为空")
    private String content;
}
