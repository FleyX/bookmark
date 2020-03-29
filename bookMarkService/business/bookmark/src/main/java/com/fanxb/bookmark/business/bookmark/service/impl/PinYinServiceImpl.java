package com.fanxb.bookmark.business.bookmark.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fanxb.bookmark.business.bookmark.dao.BookmarkDao;
import com.fanxb.bookmark.business.bookmark.entity.PinYinBody;
import com.fanxb.bookmark.business.bookmark.service.PinYinService;
import com.fanxb.bookmark.common.constant.Constant;
import com.fanxb.bookmark.common.constant.RedisConstant;
import com.fanxb.bookmark.common.entity.Bookmark;
import com.fanxb.bookmark.common.entity.redis.UserBookmarkUpdate;
import com.fanxb.bookmark.common.util.HttpUtil;
import com.fanxb.bookmark.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2020/3/18
 * Time: 23:48
 */
@Service
public class PinYinServiceImpl implements PinYinService {
    /**
     * 分隔符
     */
    private static final String PARTITION = "^|*|^";
    /**
     * 拼音接口路径
     */
    private static final String PATH = "/pinyinChange";
    /**
     * 分页查询页大小
     */
    private static final int SIZE = 500;

    @Autowired
    private BookmarkDao bookmarkDao;


    @Override
    public void changeAll() {
        int i = 0;
        while (true) {
            List<Bookmark> bookmarks = bookmarkDao.selectPinyinEmpty(i, SIZE);
            List<String> resList = changeStrings(bookmarks.stream().map(Bookmark::getName).collect(Collectors.toList()));
            for (int j = 0, size = bookmarks.size(); j < size; j++) {
                bookmarkDao.updateSearchKey(bookmarks.get(j).getBookmarkId(), resList.get(j));
            }
            if (bookmarks.size() < SIZE) {
                break;
            }
            i = bookmarks.get(SIZE - 1).getBookmarkId();
        }
        //更新所有用户的上次刷新时间
        RedisUtil.addToMq(RedisConstant.BOOKMARK_UPDATE_TIME, new UserBookmarkUpdate(-1, System.currentTimeMillis()));
    }

    @Override
    public String changeString(String str) {
        return changeStrings(Collections.singletonList(str)).get(0);
    }

    @Override
    public List<String> changeStrings(List<String> stringList) {
        Map<String, String> header = Collections.singletonMap("token", Constant.pinyinToken);
        PinYinBody body = PinYinBody.builder().strs(stringList).config(new PinYinBody.Config(false, false, 1)).build();
        JSONArray result = HttpUtil.post(Constant.pinyinBaseUrl + PATH, JSON.toJSONString(body), header, JSONArray.class);
        List<List<JSONArray>> list = result.toJavaList(JSONArray.class).stream().map(item -> item.toJavaList(JSONArray.class)).collect(Collectors.toList());
        List<String> res = new ArrayList<>(stringList.size());
        for (int i = 0, size = stringList.size(); i < size; i++) {
            List<String> pinyinList = list.get(i).stream().map(item -> item.toJavaList(String.class).get(0)).collect(Collectors.toList());
            res.add(stringList.get(i).toLowerCase() + PARTITION + CollectionUtil.join(pinyinList, "")
                    + PARTITION + pinyinList.stream().filter(item -> item.length() > 0).map(item -> item.substring(0, 1)).collect(Collectors.joining()));
        }
        return res;
    }
}
