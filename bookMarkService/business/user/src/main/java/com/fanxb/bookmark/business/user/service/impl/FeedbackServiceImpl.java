package com.fanxb.bookmark.business.user.service.impl;

import com.fanxb.bookmark.business.user.dao.FeedbackDao;
import com.fanxb.bookmark.business.user.entity.Feedback;
import com.fanxb.bookmark.business.user.service.FeedbackService;
import com.fanxb.bookmark.common.util.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2020/3/10
 * Time: 23:17
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {
    @Autowired
    private FeedbackDao feedbackDao;

    @Override
    public void addOne(Feedback feedback) {
        feedback.setUserId(UserContextHolder.get().getUserId());
        feedbackDao.insertOne(feedback);
    }
}
