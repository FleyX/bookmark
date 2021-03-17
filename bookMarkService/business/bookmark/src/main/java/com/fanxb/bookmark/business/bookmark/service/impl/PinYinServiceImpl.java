package com.fanxb.bookmark.business.bookmark.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fanxb.bookmark.business.api.UserApi;
import com.fanxb.bookmark.business.bookmark.dao.BookmarkDao;
import com.fanxb.bookmark.business.bookmark.entity.PinYinBody;
import com.fanxb.bookmark.business.bookmark.service.PinYinService;
import com.fanxb.bookmark.common.constant.Constant;
import com.fanxb.bookmark.common.constant.RedisConstant;
import com.fanxb.bookmark.common.entity.Bookmark;
import com.fanxb.bookmark.common.entity.UserContext;
import com.fanxb.bookmark.common.entity.redis.UserBookmarkUpdate;
import com.fanxb.bookmark.common.exception.CustomException;
import com.fanxb.bookmark.common.util.HttpUtil;
import com.fanxb.bookmark.common.util.RedisUtil;
import com.fanxb.bookmark.common.util.UserContextHolder;
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


    private final BookmarkDao bookmarkDao;
    private final UserApi userApi;

    @Autowired
    public PinYinServiceImpl(BookmarkDao bookmarkDao, UserApi userApi) {
        this.bookmarkDao = bookmarkDao;
        this.userApi = userApi;
    }

    @Override
    public void changeAll() {
        if (!UserContextHolder.get().getManageUser()) {
            throw new CustomException("非管理员用户，无法执行本操作");
        }
        int i = 0;
        while (true) {
            List<Bookmark> bookmarks = changeBookmarks(bookmarkDao.selectPinyinEmpty(i, SIZE));
            if (bookmarks.size() > 0) {
                bookmarkDao.updateSearchKeyBatch(bookmarks);
            }
            if (bookmarks.size() < SIZE) {
                break;
            }
            i = bookmarks.get(SIZE - 1).getBookmarkId();
        }
        //更新所有用户版本数据
        userApi.allUserVersionPlus();
    }

    @Override
    public Bookmark changeBookmark(Bookmark bookmark) {
        return changeBookmarks(Collections.singletonList(bookmark)).get(0);
    }

    @Override
    public List<Bookmark> changeBookmarks(List<Bookmark> bookmarks) {
        List<String> resList = changeStrings(bookmarks.stream().map(Bookmark::getName).collect(Collectors.toList()));
        for (int j = 0, size = bookmarks.size(); j < size; j++) {
            Bookmark bookmark = bookmarks.get(j);
            int length = bookmark.getUrl().length();
            bookmark.setSearchKey(resList.get(j) + (length == 0 ? "" : (PARTITION + bookmark.getUrl().substring(0, length > 50 ? 50 : length - 1))));
        }
        return bookmarks;
    }

    @Override
    public List<String> changeStrings(List<String> stringList) {
        Map<String, String> header = Collections.singletonMap("token", Constant.pinyinToken);
        PinYinBody body = PinYinBody.builder().strs(stringList).config(new PinYinBody.Config(false, false, 1)).build();
        JSONArray result = HttpUtil.postArray(Constant.pinyinBaseUrl + PATH, JSON.toJSONString(body), header, false);
        List<List<JSONArray>> list = result.toJavaList(JSONArray.class).stream().map(item -> item.toJavaList(JSONArray.class)).collect(Collectors.toList());
        List<String> res = new ArrayList<>(stringList.size());
        for (int i = 0, size = stringList.size(); i < size; i++) {
            List<String> pinyinList = list.get(i).stream().map(item -> item.toJavaList(String.class).get(0)).collect(Collectors.toList());
            res.add(stringList.get(i).toLowerCase() + PARTITION + CollectionUtil.join(pinyinList, "")
                    + PARTITION + pinyinList.stream().filter(item -> item.length() > 0).map(item -> item.substring(0, 1)).collect(Collectors.joining())
            );
        }
        return res;
    }
}
