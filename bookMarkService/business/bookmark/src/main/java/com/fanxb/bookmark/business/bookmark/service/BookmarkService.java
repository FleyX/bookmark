package com.fanxb.bookmark.business.bookmark.service;

import com.fanxb.bookmark.business.bookmark.entity.BookmarkEs;
import com.fanxb.bookmark.business.bookmark.entity.MoveNodeBody;
import com.fanxb.bookmark.common.entity.Bookmark;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2020/3/29
 * Time: 12:25
 */
public interface BookmarkService {
    /**
     * chrome导出书签tag
     */
    static final String DT = "dt";
    static final String A = "a";

    /**
     * Description: 根据userId和path获取书签列表
     *
     * @param userId userId
     * @param path   path
     * @return java.util.List<com.fanxb.bookmark.common.entity.Bookmark>
     * @author fanxb
     * @date 2019/7/15 13:40
     */
    List<Bookmark> getBookmarkListByPath(int userId, String path);

    /**
     * 功能描述: 获取某个用户的书签map
     *
     * @param userId userId
     * @return java.util.Map<java.lang.String, java.util.List < com.fanxb.bookmark.common.entity.Bookmark>>
     * @author fanxb
     * @date 2019/12/14 0:02
     */
    Map<String, List<Bookmark>> getOneBookmarkTree(int userId);

    /**
     * Description: 解析书签文件
     *
     * @param stream 输入流
     * @param path   存放路径
     * @author fanxb
     * @date 2019/7/9 18:44
     */
    void parseBookmarkFile(int userId, InputStream stream, String path) throws Exception;

    /**
     * Description: 详情
     *
     * @param bookmark 插入一条记录
     * @return com.fanxb.bookmark.common.entity.Bookmark
     * @author fanxb
     * @date 2019/7/12 17:18
     */
    Bookmark addOne(Bookmark bookmark);

    /**
     * Description: 编辑某个用户的某个书签
     *
     * @param userId   userId
     * @param bookmark bookmark
     * @author fanxb
     * @date 2019/7/17 14:42
     */
    void updateOne(int userId, Bookmark bookmark);

    /**
     * Description: 批量删除书签
     *
     * @param userId         用户id
     * @param folderIdList   书签文件夹id list
     * @param bookmarkIdList 书签id list
     * @author fanxb
     * @date 2019/7/12 14:09
     */
    void batchDelete(int userId, List<Integer> folderIdList, List<Integer> bookmarkIdList);

    /**
     * 功能描述: 移动一个节点
     *
     * @param userId userId
     * @param body   body
     * @author 123
     * @date 2020/3/29 12:30
     */
    void moveNode(int userId, MoveNodeBody body);

    /**
     * Description: 根据context搜索
     *
     * @param userId  userId
     * @param context context
     * @author fanxb
     * @date 2019/7/25 10:45
     */
    List<BookmarkEs> searchUserBookmark(int userId, String context);
}
