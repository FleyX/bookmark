package com.fanxb.bookmark.business.bookmark.controller;

import com.fanxb.bookmark.business.bookmark.entity.po.PInBookmarkPo;
import com.fanxb.bookmark.business.bookmark.service.HomePinService;
import com.fanxb.bookmark.common.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 首页相关接口
 *
 * @author fanxb
 */
@RestController
@RequestMapping("/home")
public class HomeController {

    private final HomePinService homePinService;

    @Autowired
    public HomeController(HomePinService homePinService) {
        this.homePinService = homePinService;
    }

    /**
     * 获取首页固定标签
     *
     * @author fanxb
     */
    @GetMapping("/pin")
    public Result getPin() {
        return Result.success(homePinService.getHomePinList());
    }

    /**
     * 新增固定书签
     *
     * @author fanxb
     */
    @PutMapping("/pin")
    public Result addPin(@RequestBody PInBookmarkPo po) {
        return Result.success(homePinService.addOne(po));
    }

    /**
     * 删除书签
     *
     * @param id url参数id
     * @author fanxb
     */
    @DeleteMapping("/pin")
    public Result deleteOne(int id) {
        homePinService.delete(id);
        return Result.success();
    }
}
