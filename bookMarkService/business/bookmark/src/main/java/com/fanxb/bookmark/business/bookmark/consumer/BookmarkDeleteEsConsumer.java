package com.fanxb.bookmark.business.bookmark.consumer;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.fanxb.bookmark.common.annotation.MqConsumer;
import com.fanxb.bookmark.common.constant.EsConstant;
import com.fanxb.bookmark.common.constant.RedisConstant;
import com.fanxb.bookmark.common.entity.redis.RedisConsumer;
import com.fanxb.bookmark.common.util.EsUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2020/3/29
 * Time: 13:08
 */
@MqConsumer(RedisConstant.BOOKMARK_DELETE_ES)
public class BookmarkDeleteEsConsumer implements RedisConsumer {
    @Autowired
    private EsUtil esUtil;

    @Override
    public void deal(String message) {
        List<String> strings = JSONArray.parseArray(message, String.class);
        if (CollectionUtil.isEmpty(strings)) {
            return;
        }
        esUtil.deleteBatch(EsConstant.BOOKMARK_INDEX, strings);
    }
}
