package com.fanxb.bookmark.business.bookmark.consumer;

import com.alibaba.fastjson.JSON;
import com.fanxb.bookmark.business.bookmark.dao.BookmarkDao;
import com.fanxb.bookmark.business.bookmark.entity.redis.VisitNumPlus;
import com.fanxb.bookmark.common.annotation.MqConsumer;
import com.fanxb.bookmark.common.constant.RedisConstant;
import com.fanxb.bookmark.common.entity.redis.RedisConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 更新书签时间
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2020/5/12
 * Time: 10:33
 *
 * @author fanxb
 */
@MqConsumer(RedisConstant.BOOKMARK_VISIT_NUM_PLUS)
@Slf4j
public class BookmarkVisitNumPlusConsumer implements RedisConsumer {

    private final BookmarkDao bookmarkDao;

    @Autowired
    public BookmarkVisitNumPlusConsumer(BookmarkDao bookmarkDao) {
        this.bookmarkDao = bookmarkDao;
    }

    @Override
    public void deal(String message) {
        VisitNumPlus item = JSON.parseObject(message, VisitNumPlus.class);
        try {
            bookmarkDao.updateVisitNum(item);
        } catch (Exception e) {
            log.error("书签访问次数增加失败：{}", e.getMessage());
        }
    }
}
