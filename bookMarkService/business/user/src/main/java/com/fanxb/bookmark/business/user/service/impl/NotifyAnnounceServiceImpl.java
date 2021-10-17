package com.fanxb.bookmark.business.user.service.impl;

import com.fanxb.bookmark.business.user.dao.NotifyAnnounceDao;
import com.fanxb.bookmark.business.user.dao.UserDao;
import com.fanxb.bookmark.business.user.entity.NotifyAnnounce;
import com.fanxb.bookmark.business.user.service.NotifyAnnounceService;
import com.fanxb.bookmark.business.user.vo.UserNotifyAnnounceRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author fanxb
 * @date 2021-10-17 14:16
 */
@Service
public class NotifyAnnounceServiceImpl implements NotifyAnnounceService {
    @Autowired
    private NotifyAnnounceDao notifyAnnounceDao;
    @Autowired
    private UserDao userDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<UserNotifyAnnounceRes> getUserAnnounce(int userId, int status) {
        notifyAnnounceDao.dealNotifyAnnounceById(userId);
        userDao.updateOneColumnByOneTerm("lastSyncAnnounceDate", System.currentTimeMillis(), "userId", userId);
        return notifyAnnounceDao.queryUserAnnounce(userId, status, System.currentTimeMillis());
    }

    @Override
    public void markAsRead(int userId, int notifyAnnounceId) {
        notifyAnnounceDao.markAsRead(userId, notifyAnnounceId, System.currentTimeMillis());
    }
}
