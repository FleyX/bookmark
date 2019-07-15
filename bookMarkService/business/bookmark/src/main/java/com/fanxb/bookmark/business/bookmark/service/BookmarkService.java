package com.fanxb.bookmark.business.bookmark.service;

import com.fanxb.bookmark.business.bookmark.dao.BookmarkDao;
import com.fanxb.bookmark.common.entity.Bookmark;
import com.fanxb.bookmark.common.exception.CustomException;
import com.fanxb.bookmark.common.util.UserContextHolder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/8 15:00
 */
@Service
public class BookmarkService {
    /**
     * chrome导出书签tag
     */
    private static final String DT = "dt";
    private static final String A = "a";

    @Autowired
    private BookmarkDao bookmarkDao;

    /**
     * Description: 解析书签文件
     *
     * @param stream 输入流
     * @param path   存放路径
     * @author fanxb
     * @date 2019/7/9 18:44
     */
    @Transactional(rollbackFor = Exception.class)
    public void parseBookmarkFile(int userId, InputStream stream, String path) throws Exception {
        Document doc = Jsoup.parse(stream, "utf-8", "");
        Elements elements = doc.select("html>body>dl>dt");
        //获取当前层sort最大值
        Integer sortBase = bookmarkDao.selectMaxSort(1, path);
        if (sortBase == null) {
            sortBase = 0;
        }
        int count = 0;
        for (int i = 0, length = elements.size(); i < length; i++) {
            if (i == 0) {
                Elements firstChildren = elements.get(0).child(1).children();
                count = firstChildren.size();
                for (int j = 0; j < count; j++) {
                    dealBookmark(userId, firstChildren.get(j), path, sortBase + j);
                }
            } else {
                dealBookmark(userId, elements.get(i), path, sortBase + count + i - 1);
            }
        }
    }


    /**
     * Description: 根据userId和path获取书签列表
     *
     * @param userId userId
     * @param path   path
     * @return java.util.List<com.fanxb.bookmark.common.entity.Bookmark>
     * @author fanxb
     * @date 2019/7/15 13:40
     */
    public List<Bookmark> getBookmarkListByPath(int userId, String path) {
        return bookmarkDao.getListByUserIdAndPath(userId, path);
    }


    /**
     * Description: 批量删除书签
     *
     * @param userId         用户id
     * @param folderIdList   书签文件夹id list
     * @param bookmarkIdList 书签id list
     * @author fanxb
     * @date 2019/7/12 14:09
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(int userId, List<Integer> folderIdList, List<Integer> bookmarkIdList) {
        for (Integer item : folderIdList) {
            bookmarkDao.deleteUserFolder(userId, item);
            bookmarkIdList.add(item);
        }
        bookmarkDao.deleteUserBookmark(userId, bookmarkIdList);
    }

    /**
     * Description: 详情
     *
     * @param bookmark 插入一条记录
     * @return com.fanxb.bookmark.common.entity.Bookmark
     * @author fanxb
     * @date 2019/7/12 17:18
     */
    public Bookmark addOne(Bookmark bookmark) {
        int userId = UserContextHolder.get().getUserId();
        Integer sort = bookmarkDao.selectMaxSort(userId, bookmark.getPath());
        bookmark.setSort(sort == null ? 1 : sort + 1);
        bookmark.setUserId(userId);
        bookmark.setCreateTime(System.currentTimeMillis());
        bookmark.setAddTime(bookmark.getCreateTime());
        try {
            bookmarkDao.insertOne(bookmark);
        } catch (DuplicateKeyException e) {
            throw new CustomException("同级目录下不能存在相同名称的数据");
        }
        return bookmark;
    }


    /**
     * Description: 处理html节点，解析出文件夹和书签
     *
     * @param ele  待处理节点
     * @param path 节点路径，不包含自身
     * @param sort 当前层级中的排序序号
     * @author fanxb
     * @date 2019/7/8 14:49
     */
    private void dealBookmark(int userId, Element ele, String path, int sort) {
        if (!DT.equalsIgnoreCase(ele.tagName())) {
            return;
        }
        Element first = ele.child(0);
        if (A.equalsIgnoreCase(first.tagName())) {
            //说明为链接
            Bookmark node = new Bookmark(userId, path, first.ownText(), first.attr("href"), first.attr("icon")
                    , Long.valueOf(first.attr("add_date")) * 1000, sort);
            //存入数据库
            insertOne(node);
        } else {
            //说明为文件夹
            Bookmark node = new Bookmark(userId, path, first.ownText(), Long.valueOf(first.attr("add_date")) * 1000, sort);
            Integer sortBase = 0;
            if (insertOne(node)) {
                sortBase = bookmarkDao.selectMaxSort(node.getUserId(), path);
                if (sortBase == null) {
                    sortBase = 0;
                }
            }
            String childPath = path + "." + node.getBookmarkId();
            Elements children = ele.child(1).children();
            for (int i = 0, size = children.size(); i < size; i++) {
                dealBookmark(userId, children.get(i), childPath, sortBase + i + 1);
            }
        }
    }

    /**
     * Description: 插入一条书签，如果已经存在同名书签将跳过
     *
     * @param node node
     * @return boolean 如果已经存在返回true，否则false
     * @author fanxb
     * @date 2019/7/8 17:25
     */
    private boolean insertOne(Bookmark node) {
        //先根据name,userId,parentId获取此节点id
        Integer id = bookmarkDao.selectIdByUserIdAndNameAndPath(node.getUserId(), node.getName(), node.getPath());
        if (id == null) {
            bookmarkDao.insertOne(node);
            return false;
        } else {
            node.setBookmarkId(id);
            return true;
        }
    }

}
