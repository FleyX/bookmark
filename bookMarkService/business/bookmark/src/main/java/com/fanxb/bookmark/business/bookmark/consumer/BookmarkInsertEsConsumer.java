package com.fanxb.bookmark.business.bookmark.consumer;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.fanxb.bookmark.business.bookmark.entity.BookmarkEs;
import com.fanxb.bookmark.common.annotation.MqConsumer;
import com.fanxb.bookmark.common.constant.EsConstant;
import com.fanxb.bookmark.common.constant.RedisConstant;
import com.fanxb.bookmark.common.entity.Bookmark;
import com.fanxb.bookmark.common.entity.EsEntity;
import com.fanxb.bookmark.common.entity.redis.RedisConsumer;
import com.fanxb.bookmark.common.util.EsUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 插入/更新数据到es中
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2020/3/29
 * Time: 11:34
 * @author fanxb
 */
@MqConsumer(RedisConstant.BOOKMARK_INSERT_ES)
public class BookmarkInsertEsConsumer implements RedisConsumer {


    @Autowired
    private EsUtil esUtil;

    @Override
    public void deal(String message) {
        List<Bookmark> bookmarks = JSONArray.parseArray(message, Bookmark.class);
        if (CollectionUtil.isEmpty(bookmarks)) {
            return;
        }
        List<EsEntity<BookmarkEs>> esList = bookmarks.stream()
                .map(item -> new EsEntity<>(item.getBookmarkId().toString(), new BookmarkEs(item))).collect(Collectors.toList());
        esUtil.insertBatch(EsConstant.BOOKMARK_INDEX, esList);
    }
}
