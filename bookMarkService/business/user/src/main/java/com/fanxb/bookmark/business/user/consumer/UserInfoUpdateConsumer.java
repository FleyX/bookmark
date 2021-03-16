package com.fanxb.bookmark.business.user.consumer;

import com.alibaba.fastjson.JSON;
import com.fanxb.bookmark.business.user.dao.UserDao;
import com.fanxb.bookmark.common.annotation.MqConsumer;
import com.fanxb.bookmark.common.constant.RedisConstant;
import com.fanxb.bookmark.common.entity.redis.RedisConsumer;
import com.fanxb.bookmark.common.entity.redis.UserBookmarkUpdate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author fanxb
 * @date 2020/1/26 上午11:54
 */
@MqConsumer(RedisConstant.BOOKMARK_UPDATE_VERSION)
public class UserInfoUpdateConsumer implements RedisConsumer {

    @Autowired
    private UserDao userDao;

    @Override
    public void deal(String message) {
//        int userId = Integer.parseInt(message);
//        if (userId == -1) {
//            userDao.updateAllBookmarkUpdateVersion();
//        } else {
//            userDao.updateLastBookmarkUpdateTime(userId);
//        }
    }
}
