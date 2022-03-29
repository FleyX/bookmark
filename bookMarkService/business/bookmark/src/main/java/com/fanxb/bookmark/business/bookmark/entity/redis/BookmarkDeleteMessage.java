package com.fanxb.bookmark.business.bookmark.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Collection;
import java.util.List;

/**
 * @author fanxb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDeleteMessage {
    /**
     * 用户id
     */
    private int userId;
    /**
     * 批量删除的书签id
     */
    private Collection<String> bookmarkIds;
}
