package com.fanxb.bookmark.business.api;

import java.util.Set;

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

    /***
     * 删除一次数据
     * @author fanxb
     * @param delete 是否删除问题数据
     * @param userId userId
     * @date 2021/3/17
     **/
    Set<String> dealBadBookmark(boolean delete, int userId);
}
