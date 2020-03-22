package com.fanxb.bookmark.business.bookmark.controller;

import com.fanxb.bookmark.business.bookmark.entity.BatchDeleteBody;
import com.fanxb.bookmark.business.bookmark.entity.BookmarkEs;
import com.fanxb.bookmark.business.bookmark.entity.MoveNodeBody;
import com.fanxb.bookmark.business.bookmark.service.BookmarkService;
import com.fanxb.bookmark.business.bookmark.service.PinYinService;
import com.fanxb.bookmark.common.entity.Bookmark;
import com.fanxb.bookmark.common.entity.Result;
import com.fanxb.bookmark.common.entity.UserContext;
import com.fanxb.bookmark.common.util.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/8 15:12
 */
@RestController
@RequestMapping("/bookmark")
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;
    @Autowired
    private PinYinService pinYinService;

    /**
     * Description: 获取路径为path的书签数据
     *
     * @param path 路径
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/7/15 13:36
     */
    @GetMapping("/currentUser/path")
    public Result getCurrentBookmarkList(String path) {
        return Result.success(bookmarkService.getBookmarkListByPath(UserContextHolder.get().getUserId(), path));
    }

    /**
     * 功能描述: 获取当前登陆用户的书签树
     *
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/12/14 0:09
     */
    @GetMapping("/currentUser")
    public Result getBookmarkMap() {
        return Result.success(bookmarkService.getOneBookmarkTree(UserContextHolder.get().getUserId()));
    }

    /**
     * Description: 上传书签备份文件，并解析
     *
     * @param file 文件
     * @param path 存放节点路径(根节点为空字符串）
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/7/8 15:17
     */
    @PutMapping("/uploadBookmarkFile")
    public Result uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("path") String path) throws Exception {
        bookmarkService.parseBookmarkFile(UserContextHolder.get().getUserId(), file.getInputStream(), path);
        return Result.success(null);
    }

    /**
     * Description: 新增一条书签
     *
     * @param bookmark 书签信息
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/7/12 17:28
     */
    @PutMapping("")
    public Result addOne(@RequestBody Bookmark bookmark) {
        bookmark = bookmarkService.addOne(bookmark);
        return Result.success(bookmark);
    }


    /**
     * Description: 编辑当前用户的一个书签节点
     *
     * @param bookmark bookmark
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/7/17 14:40
     */
    @PostMapping("/updateOne")
    public Result editCurrentUserBookmark(@RequestBody Bookmark bookmark) {
        bookmarkService.updateOne(UserContextHolder.get().getUserId(), bookmark);
        return Result.success(null);
    }

    /**
     * Description: 批量删除
     *
     * @param body 批量删除表单
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/7/12 17:28
     */
    @PostMapping("/batchDelete")
    public Result batchDelete(@RequestBody BatchDeleteBody body) {
        bookmarkService.batchDelete(UserContextHolder.get().getUserId(), body.getFolderIdList(), body.getBookmarkIdList());
        return Result.success(null);
    }

    /**
     * Description: 移动一个节点
     *
     * @param body body
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/7/18 10:54
     */
    @PostMapping("/moveNode")
    public Result moveNode(@RequestBody MoveNodeBody body) {
        bookmarkService.moveNode(UserContextHolder.get().getUserId(), body);
        return Result.success(null);
    }

    @GetMapping("/searchUserBookmark")
    public Result searchUserBookmark(String content) {
        List<BookmarkEs> res = bookmarkService.searchUserBookmark(UserContextHolder.get().getUserId(), content);
        return Result.success(res);
    }

    /**
     * Description: 同步当前用户的书签到es中
     *
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/7/26 15:33
     */
    @PostMapping("/syncBookmark")
    public Result syncBookmark() {
        bookmarkService.syncUserBookmark(UserContextHolder.get().getUserId());
        return Result.success(null);
    }

    /**
     * 功能描述: 全量更新拼音数据
     *
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2020/3/22 22:22
     */
    @PostMapping("/allPinyinCreate")
    public Result changeAllPinyin() {
        pinYinService.changeAll();
        return Result.success(null);
    }

}
