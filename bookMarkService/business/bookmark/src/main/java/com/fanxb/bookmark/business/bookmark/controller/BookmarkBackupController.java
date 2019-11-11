package com.fanxb.bookmark.business.bookmark.controller;

import com.fanxb.bookmark.business.bookmark.service.BookmarkBackupService;
import com.fanxb.bookmark.common.entity.Result;
import com.fanxb.bookmark.common.util.ThreadPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2019/11/12
 * Time: 0:35
 *
 * @author fanxb
 */
@RestController
@RequestMapping("/bookmarkBackup")
public class BookmarkBackupController {

    private BookmarkBackupService backupService;

    @Autowired
    public BookmarkBackupController(BookmarkBackupService backupService) {
        this.backupService = backupService;
    }

    @PostMapping("/mysqlToEs")
    public Result backupToEs() {
        //异步执行同步任务
        ThreadPoolUtil.execute(() -> backupService.backupToEs());
        return Result.success(null);
    }
}
