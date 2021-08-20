package com.fanxb.bookmark.business.user.service;

import com.fanxb.bookmark.business.user.entity.Feedback;

/**
 * Created with IntelliJ IDEA
 *
 * @author fanxb
 * Date: 2020/3/10
 * Time: 23:17
 */
public interface FeedbackService {
    /**
     * 功能描述:  插入一条记录
     *
     * @param feedback feedback
     * @author fanxb
     * @date 2020/3/10 23:18
     */
    void addOne(Feedback feedback);
}
