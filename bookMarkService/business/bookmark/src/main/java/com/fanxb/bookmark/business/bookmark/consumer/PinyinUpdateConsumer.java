package com.fanxb.bookmark.business.bookmark.consumer;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.fanxb.bookmark.business.bookmark.dao.BookmarkDao;
import com.fanxb.bookmark.business.bookmark.service.PinYinService;
import com.fanxb.bookmark.common.annotation.MqConsumer;
import com.fanxb.bookmark.common.constant.RedisConstant;
import com.fanxb.bookmark.common.entity.Bookmark;
import com.fanxb.bookmark.common.entity.redis.RedisConsumer;
import com.fanxb.bookmark.common.entity.redis.UserBookmarkUpdate;
import com.fanxb.bookmark.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 平应转换
 * Created By Fxb
 * Date: 2020/3/29
 * Time: 13:01
 */
@MqConsumer(RedisConstant.BOOKMARK_PINYIN_CHANGE)
public class PinyinUpdateConsumer implements RedisConsumer {

    @Autowired
    private PinYinService pinYinService;
    @Autowired
    private BookmarkDao bookmarkDao;

    @Override
    public void deal(String message) {
        List<Bookmark> bookmarks = JSONArray.parseArray(message, Bookmark.class);
        if (CollectionUtil.isEmpty(bookmarks)) {
            return;
        }
        List<String> resList = pinYinService.changeStrings(bookmarks.stream().map(Bookmark::getName).collect(Collectors.toList()));
        for (int i = 0, size = bookmarks.size(); i < size; i++) {
            bookmarkDao.updateSearchKey(bookmarks.get(i).getBookmarkId(), resList.get(i));
        }
        //更新本用户书签更新时间
        RedisUtil.addToMq(RedisConstant.BOOKMARK_UPDATE_TIME, new UserBookmarkUpdate(bookmarks.get(0).getUserId()));
    }
}
