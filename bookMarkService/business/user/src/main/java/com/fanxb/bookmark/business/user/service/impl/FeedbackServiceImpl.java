package com.fanxb.bookmark.business.user.service.impl;

import com.fanxb.bookmark.business.user.dao.FeedbackDao;
import com.fanxb.bookmark.business.user.entity.Feedback;
import com.fanxb.bookmark.business.user.service.FeedbackService;
import com.fanxb.bookmark.common.util.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA
 *
 * @author fanxb
 * Date: 2020/3/10
 * Time: 23:17
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {
    private final FeedbackDao feedbackDao;

    @Autowired
    public FeedbackServiceImpl(FeedbackDao feedbackDao) {
        this.feedbackDao = feedbackDao;
    }

    @Override
    public void addOne(Feedback feedback) {
        feedback.setUserId(UserContextHolder.get().getUserId());
        feedbackDao.insertOne(feedback);
    }
}
