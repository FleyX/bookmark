package com.fanxb.bookmark.business.user.controller;

import com.fanxb.bookmark.business.user.service.NotifyAnnounceService;
import com.fanxb.bookmark.common.entity.Result;
import com.fanxb.bookmark.common.util.UserContextHolder;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author fanxb
 * @date 2021-10-17 14:03
 */
@RestController
@RequestMapping("/announce/user")
public class NotifyAnnounceController {
    @Autowired
    private NotifyAnnounceService notifyAnnounceService;

    /**
     * 获取站内信
     */
    @GetMapping
    public Result getUserAnnounce(@RequestParam int status) {
        return Result.success(notifyAnnounceService.getUserAnnounce(UserContextHolder.get().getUserId(), status));
    }

    /**
     * 标记为已读
     */
    @PostMapping("/read")
    public Result readNotifyAnnounce(@RequestParam int notifyAnnounceId) {
        notifyAnnounceService.markAsRead(UserContextHolder.get().getUserId(), notifyAnnounceId);
        return Result.success();
    }
}
