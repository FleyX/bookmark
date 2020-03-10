package com.fanxb.bookmark.business.user.controller;

import com.fanxb.bookmark.business.user.entity.Feedback;
import com.fanxb.bookmark.business.user.service.FeedbackService;
import com.fanxb.bookmark.common.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2020/3/10
 * Time: 23:16
 */
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PutMapping("")
    public Result addone(@Validated @RequestBody Feedback feedback) {
        feedbackService.addOne(feedback);
        return Result.success(null);
    }
}
