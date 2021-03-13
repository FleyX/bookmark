package com.fanxb.bookmark.business.api;

/**
 * @author fanxb
 * @date 2021/3/13
 **/
public interface BookmarkApi {
    /**
     * 更新某个用户的icon数据
     *
     * @param userId 用户id
     * @author fanxb
     * @date 2021/3/11
     **/
    void updateUserBookmarkIcon(int userId);
}
