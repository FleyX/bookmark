package com.fanxb.bookmark.business.bookmark.entity;

import com.fanxb.bookmark.common.entity.Bookmark;
import com.fanxb.bookmark.common.entity.EsInsertEntity;
import lombok.Data;

/**
 * 类功能简述： 书签在es中的存储结构
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/24 15:07
 */
@Data
public class BookmarkEs extends EsInsertEntity<BookmarkEs> {
    private Integer bookmarkId;
    private Integer userId;
    private String name;
    private String url;

    public BookmarkEs() {
        super();
    }

    public BookmarkEs(Bookmark bookmark) {
        super();
        this.setData(this);
        this.bookmarkId = bookmark.getBookmarkId();
        this.userId = bookmark.getUserId();
        this.name = bookmark.getName();
        this.url = bookmark.getUrl();
    }
}
