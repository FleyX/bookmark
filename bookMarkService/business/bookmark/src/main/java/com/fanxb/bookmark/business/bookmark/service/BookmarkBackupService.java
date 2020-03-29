package com.fanxb.bookmark.business.bookmark.service;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2020/3/29
 * Time: 12:43
 */
public interface BookmarkBackupService {


    /**
     * 功能描述: 将mysql数据同步到es中
     *
     * @author fanxb
     * @date 2019/11/12 0:22
     */
    void backupToEs();

    /**
     * Description: 将某个用户的书签数据mysql同步到es中
     *
     * @author fanxb
     * @date 2019/7/26 11:27
     */
    void syncUserBookmark(int userId);
}
