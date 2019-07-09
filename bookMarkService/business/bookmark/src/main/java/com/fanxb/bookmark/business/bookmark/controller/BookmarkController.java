package com.fanxb.bookmark.business.bookmark.controller;

import com.fanxb.bookmark.business.bookmark.service.BookmarkService;
import com.fanxb.bookmark.common.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * Description: 获取某个用户的节点树
     *
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/7/9 14:20
     */
    @GetMapping("/currentUser")
    public Result getUserBookmarkTree() {
        return null;
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
        bookmarkService.parseBookmarkFile(file.getInputStream(), path);
        return Result.success(null);
    }

}
