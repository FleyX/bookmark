package com.fanxb.bookmark.business.bookmark.service;

import com.fanxb.bookmark.common.entity.Bookmark;

import java.util.List;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2020/3/18
 * Time: 23:47
 */
public interface PinYinService {

    /**
     * 分隔符
     */
    static final String PARTITION = "||";
    /**
     * 拼音接口路径
     */
    static final String PATH = "/pinyinChange";
    /**
     * 分页查询页大小
     */
    static final int SIZE = 500;

    /**
     * 功能描述: 首次上线用于全量初始化
     *
     * @author fanxb
     * @date 2020/3/22 21:40
     */
    void changeAll();

    /**
     * 处理bookmark searchKey
     *
     * @param bookmark 处理单个
     * @return java.util.List<com.fanxb.bookmark.common.entity.Bookmark>
     * @author fanxb
     * @date 2021/3/13
     **/
    Bookmark changeBookmark(Bookmark bookmark);

    /**
     * 处理bookmarks searchKey
     *
     * @param bookmarks 待处理舒淇啊你列表
     * @return java.util.List<com.fanxb.bookmark.common.entity.Bookmark>
     * @author fanxb
     * @date 2021/3/13
     **/
    List<Bookmark> changeBookmarks(List<Bookmark> bookmarks);

    /**
     * 功能描述:返回用于前端搜索的key
     *
     * @param stringList 待拼音化的字符串
     * @return java.util.List<java.lang.String>
     * @author fanxb
     * @date 2020/3/22 21:38
     */
    List<String> changeStrings(List<String> stringList);
}
