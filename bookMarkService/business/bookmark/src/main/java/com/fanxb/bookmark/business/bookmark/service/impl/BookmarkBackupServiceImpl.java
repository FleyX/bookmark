package com.fanxb.bookmark.business.bookmark.service.impl;

import com.fanxb.bookmark.business.bookmark.dao.BookmarkDao;
import com.fanxb.bookmark.business.bookmark.entity.BookmarkEs;
import com.fanxb.bookmark.business.bookmark.service.BookmarkBackupService;
import com.fanxb.bookmark.common.constant.EsConstant;
import com.fanxb.bookmark.common.entity.Bookmark;
import com.fanxb.bookmark.common.entity.EsEntity;
import com.fanxb.bookmark.common.util.EsUtil;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2019/11/12
 * Time: 0:22
 *
 * @author fanxb
 */
@Service
public class BookmarkBackupServiceImpl implements BookmarkBackupService {

    @Autowired
    private BookmarkDao bookmarkDao;

    @Autowired
    private EsUtil esUtil;

    /**
     * 一次同步BACKUP_SIZE条到es中
     */
    private static final int BACKUP_SIZE = 500;

    @Override
    public void backupToEs() {
        int start = 0;
        List<Bookmark> list;
        while ((list = bookmarkDao.getBookmarkListPage(BACKUP_SIZE, start)).size() != 0) {
            List<EsEntity<BookmarkEs>> batchList = new ArrayList<>(list.size());
            list.forEach(item -> batchList.add(new EsEntity<>(item.getBookmarkId().toString(), new BookmarkEs(item))));
            esUtil.insertBatch(EsConstant.BOOKMARK_INDEX, batchList);
            start += BACKUP_SIZE;
        }
    }

    @Override
    public void syncUserBookmark(int userId) {
        //删除旧的数据
        esUtil.deleteByQuery(EsConstant.BOOKMARK_INDEX, new TermQueryBuilder("userId", userId));
        int index = 0;
        int size = 500;
        List<EsEntity<BookmarkEs>> res = new ArrayList<>();
        do {
            res.clear();
            bookmarkDao.selectBookmarkEsByUserIdAndType(userId, 0, index, size)
                    .forEach(item -> res.add(new EsEntity<>(item.getBookmarkId().toString(), item)));
            if (res.size() > 0) {
                esUtil.insertBatch(EsConstant.BOOKMARK_INDEX, res);
            }
            index += size;
        } while (res.size() == 500);

    }
}
