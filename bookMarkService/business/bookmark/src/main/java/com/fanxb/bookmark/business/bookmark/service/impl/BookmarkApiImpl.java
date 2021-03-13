package com.fanxb.bookmark.business.bookmark.service.impl;

import com.fanxb.bookmark.business.api.BookmarkApi;
import com.fanxb.bookmark.business.bookmark.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * bookmark模块api暴露
 *
 * @author fanxb
 * @date 2021/3/13
 **/
@Service
public class BookmarkApiImpl implements BookmarkApi {
    private final BookmarkService bookmarkService;

    @Autowired
    public BookmarkApiImpl(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @Override
    public void updateUserBookmarkIcon(int userId) {
        bookmarkService.updateUserBookmarkIcon(userId);
    }
}
