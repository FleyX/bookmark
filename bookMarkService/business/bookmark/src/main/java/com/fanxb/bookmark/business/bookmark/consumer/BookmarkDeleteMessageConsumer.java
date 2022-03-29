package com.fanxb.bookmark.business.bookmark.consumer;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fanxb.bookmark.business.bookmark.dao.PinBookmarkDao;
import com.fanxb.bookmark.business.bookmark.entity.redis.BookmarkDeleteMessage;
import com.fanxb.bookmark.common.annotation.MqConsumer;
import com.fanxb.bookmark.common.constant.EsConstant;
import com.fanxb.bookmark.common.constant.RedisConstant;
import com.fanxb.bookmark.common.entity.redis.RedisConsumer;
import com.fanxb.bookmark.common.util.EsUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA
 *
 * @author fanxb
 * Date: 2020/3/29
 * Time: 13:08
 */
@MqConsumer(RedisConstant.BOOKMARK_DELETE_ES)
public class BookmarkDeleteMessageConsumer implements RedisConsumer {
    private final PinBookmarkDao pinBookmarkDao;
    private final EsUtil esUtil;

    @Autowired
    public BookmarkDeleteMessageConsumer(PinBookmarkDao pinBookmarkDao, EsUtil esUtil) {
        this.pinBookmarkDao = pinBookmarkDao;
        this.esUtil = esUtil;
    }

    @Override
    public void deal(String message) {
        BookmarkDeleteMessage obj = JSON.parseObject(message, BookmarkDeleteMessage.class);
        //删除首页固定的数据
        pinBookmarkDao.deleteUnExistBookmark(obj.getUserId());
        //删除es数据
        if (CollectionUtil.isNotEmpty(obj.getBookmarkIds())) {
            esUtil.deleteBatch(EsConstant.BOOKMARK_INDEX, obj.getBookmarkIds());
        }
    }
}
