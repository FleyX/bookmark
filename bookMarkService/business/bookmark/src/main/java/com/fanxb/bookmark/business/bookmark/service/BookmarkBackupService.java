package com.fanxb.bookmark.business.bookmark.service;

import com.fanxb.bookmark.business.bookmark.dao.BookmarkBackupDao;
import com.fanxb.bookmark.business.bookmark.dao.BookmarkDao;
import com.fanxb.bookmark.common.constant.EsConstant;
import com.fanxb.bookmark.common.entity.Bookmark;
import com.fanxb.bookmark.common.entity.EsEntity;
import com.fanxb.bookmark.common.util.EsUtil;
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
public class BookmarkBackupService {

    @Autowired
    private BookmarkDao bookmarkDao;
    @Autowired
    private BookmarkBackupDao bookmarkBackupDao;

    @Autowired
    private EsUtil esUtil;

    /**
     * 一次同步BACKUP_SIZE条到es中
     */
    private static final int BACKUP_SIZE = 500;

    /**
     * 功能描述: 将mysql数据同步到es中
     *
     * @author fanxb
     * @date 2019/11/12 0:22
     */
    public void backupToEs() {
        int start = 0;
        List<Bookmark> list;
        while ((list = bookmarkBackupDao.getBookmarkListPage(BACKUP_SIZE, start)).size() != 0) {
            List<EsEntity> batchList = new ArrayList<>(list.size());
            list.forEach(item -> batchList.add(new EsEntity<>(item.getBookmarkId().toString(), item)));
            esUtil.insertBatch(EsConstant.BOOKMARK_INDEX, batchList);
            start += BACKUP_SIZE;
        }
    }
}
