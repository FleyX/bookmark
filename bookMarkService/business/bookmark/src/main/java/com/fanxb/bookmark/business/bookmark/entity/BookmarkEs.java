package com.fanxb.bookmark.business.bookmark.entity;

import com.fanxb.bookmark.common.entity.Bookmark;
import lombok.Data;

/**
 * 类功能简述： 书签在es中的存储结构
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/24 15:07
 */
@Data
public class BookmarkEs {
    private Integer userId;
    private Integer bookmarkId;
    private String name;
    private String url;

    public BookmarkEs() {
    }

    public BookmarkEs(Bookmark bookmark) {
        this.setBookmarkId(bookmark.getBookmarkId());
        this.userId = bookmark.getUserId();
        this.name = bookmark.getName();
        this.url = bookmark.getUrl();
    }
}
