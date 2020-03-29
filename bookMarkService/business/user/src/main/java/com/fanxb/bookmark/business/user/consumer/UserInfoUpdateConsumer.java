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
@MqConsumer(RedisConstant.BOOKMARK_UPDATE_TIME)
public class UserInfoUpdateConsumer implements RedisConsumer {

    @Autowired
    private UserDao userDao;

    @Override
    public void deal(String message) {
        UserBookmarkUpdate item = JSON.parseObject(message, UserBookmarkUpdate.class);
        if (item.getUserId() == -1) {
            userDao.updateAllBookmarkUpdateTime(item.getUpdateTime());
        } else {
            userDao.updateLastBookmarkUpdateTime(item);
        }
    }
}
