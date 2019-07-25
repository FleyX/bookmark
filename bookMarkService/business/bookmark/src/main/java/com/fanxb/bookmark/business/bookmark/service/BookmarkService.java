package com.fanxb.bookmark.business.bookmark.service;

import com.fanxb.bookmark.business.bookmark.dao.BookmarkDao;
import com.fanxb.bookmark.business.bookmark.entity.BookmarkEs;
import com.fanxb.bookmark.business.bookmark.entity.MoveNodeBody;
import com.fanxb.bookmark.common.constant.EsConstant;
import com.fanxb.bookmark.common.entity.Bookmark;
import com.fanxb.bookmark.common.entity.EsEntity;
import com.fanxb.bookmark.common.exception.CustomException;
import com.fanxb.bookmark.common.util.EsUtil;
import com.fanxb.bookmark.common.util.UserContextHolder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Autowired
    private EsUtil esUtil;

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
        // 將要插入es的书签数据放到list中,最后一次插入，尽量避免mysql回滚了，但是es插入了
        List<EsEntity> insertEsList = new ArrayList<>();
        for (int i = 0, length = elements.size(); i < length; i++) {
            if (i == 0) {
                Elements firstChildren = elements.get(0).child(1).children();
                count = firstChildren.size();
                for (int j = 0; j < count; j++) {
                    dealBookmark(userId, firstChildren.get(j), path, sortBase + j, insertEsList);
                }
            } else {
                dealBookmark(userId, elements.get(i), path, sortBase + count + i - 1, insertEsList);
            }
        }
        esUtil.insertBatch(EsConstant.BOOKMARK_INDEX, insertEsList);
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
    private void dealBookmark(int userId, Element ele, String path, int sort, List<EsEntity> insertList) {
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
            insertList.add(new EsEntity<>(node.getBookmarkId().toString(), new BookmarkEs(node)));
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
                dealBookmark(userId, children.get(i), childPath, sortBase + i + 1, insertList);
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
        Set<Integer> set = new HashSet<>();
        for (Integer item : folderIdList) {
            set.addAll(bookmarkDao.getChildrenBookmarkId(userId, item));
            bookmarkDao.deleteUserFolder(userId, item);
            bookmarkIdList.add(item);
        }
        if (bookmarkIdList.size() > 0) {
            bookmarkDao.deleteUserBookmark(userId, bookmarkIdList);
        }
        set.addAll(bookmarkIdList);
        //es 中批量删除
        esUtil.deleteBatch(EsConstant.BOOKMARK_INDEX, set);
    }

    /**
     * Description: 详情
     *
     * @param bookmark 插入一条记录
     * @return com.fanxb.bookmark.common.entity.Bookmark
     * @author fanxb
     * @date 2019/7/12 17:18
     */
    @Transactional(rollbackFor = Exception.class)
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
        //如果是书签，插入到es中
        if (bookmark.getType() == 0) {
            esUtil.insertOrUpdateOne(EsConstant.BOOKMARK_INDEX,
                    new EsEntity<>(bookmark.getBookmarkId().toString(), new BookmarkEs(bookmark)));
        }
        return bookmark;
    }

    /**
     * Description: 编辑某个用户的某个书签
     *
     * @param userId   userId
     * @param bookmark bookmark
     * @author fanxb
     * @date 2019/7/17 14:42
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateOne(int userId, Bookmark bookmark) {
        bookmark.setUserId(userId);
        bookmarkDao.editBookmark(bookmark);
        if (bookmark.getType() == 0) {
            esUtil.insertOrUpdateOne(EsConstant.BOOKMARK_INDEX,
                    new EsEntity<>(bookmark.getBookmarkId().toString(), new BookmarkEs(bookmark)));
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void moveNode(int userId, MoveNodeBody body) {
        if (body.getSort() == -1) {
            Integer max = bookmarkDao.selectMaxSort(userId, body.getTargetPath());
            body.setSort(max == null ? 1 : max);
        } else {
            //更新目标节点的sort
            bookmarkDao.sortPlus(userId, body.getTargetPath(), body.getSort());
        }
        //如果目标位置和当前位置不在一个层级中需要更新子节点的path
        if (!body.getTargetPath().equals(body.getSourcePath())) {
            bookmarkDao.updateChildrenPath(userId, body.getSourcePath() + "." + body.getBookmarkId()
                    , body.getTargetPath() + "." + body.getBookmarkId());
        }
        //更新被移动节点的path和sort
        bookmarkDao.updatePathAndSort(userId, body.getBookmarkId(), body.getTargetPath(), body.getSort());
    }

    /**
     * Description: 根据context搜索
     *
     * @param userId  userId
     * @param context context
     * @author fanxb
     * @date 2019/7/25 10:45
     */
    public List<BookmarkEs> searchUserBookmark(int userId, String context) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.termQuery("userId", userId));
        boolQueryBuilder.must(QueryBuilders.multiMatchQuery(context, "name", "url"));
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.size(5);
        builder.query(boolQueryBuilder);
        List<BookmarkEs> list = esUtil.search(EsConstant.BOOKMARK_INDEX, builder, BookmarkEs.class);
        return list;
    }

}
