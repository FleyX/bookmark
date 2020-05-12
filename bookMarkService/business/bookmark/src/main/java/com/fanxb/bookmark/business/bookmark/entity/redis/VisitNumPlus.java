package com.fanxb.bookmark.business.bookmark.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created with IntelliJ IDEA
 *
 * @author fanxb
 * Date: 2020/5/12 11:47
 */
@Data
@AllArgsConstructor
public class VisitNumPlus {
    /**
     * 用户id
     */
    private int userId;
    /**
     * 书签id
     */
    private int bookmarkId;
}
