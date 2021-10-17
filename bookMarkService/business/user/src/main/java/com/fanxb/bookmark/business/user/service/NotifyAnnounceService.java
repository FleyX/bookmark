package com.fanxb.bookmark.business.user.service;

import com.fanxb.bookmark.business.user.entity.NotifyAnnounce;
import com.fanxb.bookmark.business.user.vo.UserNotifyAnnounceRes;

import java.util.List;

/**
 * @author fanxb
 * @date 2021-10-17 14:07
 */
public interface NotifyAnnounceService {

    /**
     * 获取用户通知
     *
     * @param userId 用户id
     * @param status 状态 0:未读，1:已读
     * @author fanxb
     * @date 2021/10/17 14:14
     */
    List<UserNotifyAnnounceRes> getUserAnnounce(int userId, int status);

    /**
     * 标记为已读
     *
     * @param userId           用户id
     * @param notifyAnnounceId 通知id
     * @author fanxb
     * @date 2021/10/17 14:14
     */
    void markAsRead(int userId, int notifyAnnounceId);
}
