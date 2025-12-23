package com.fanxb.bookmark.business.bookmark.consumer;

import com.alibaba.fastjson.JSON;
import com.fanxb.bookmark.business.bookmark.dao.PinBookmarkDao;
import com.fanxb.bookmark.business.bookmark.entity.redis.BookmarkDeleteMessage;
import com.fanxb.bookmark.common.annotation.MqConsumer;
import com.fanxb.bookmark.common.constant.RedisConstant;
import com.fanxb.bookmark.common.entity.redis.RedisConsumer;

import lombok.RequiredArgsConstructor;

/**
 * Created with IntelliJ IDEA
 *
 * @author fanxb
 *         Date: 2020/3/29
 *         Time: 13:08
 */
@MqConsumer(RedisConstant.BOOKMARK_DELETE_ES)
@RequiredArgsConstructor
public class BookmarkDeleteMessageConsumer implements RedisConsumer {
    private final PinBookmarkDao pinBookmarkDao;

    @Override
    public void deal(String message) {
        BookmarkDeleteMessage obj = JSON.parseObject(message, BookmarkDeleteMessage.class);
        // 删除首页固定的数据
        pinBookmarkDao.deleteUnExistBookmark(obj.getUserId());
    }
}
