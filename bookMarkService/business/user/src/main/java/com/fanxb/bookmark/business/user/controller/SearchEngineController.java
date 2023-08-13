package com.fanxb.bookmark.business.user.controller;

import com.fanxb.bookmark.business.user.dao.SearchEngineDao;
import com.fanxb.bookmark.business.user.entity.SearchEngine;
import com.fanxb.bookmark.business.user.service.SearchEngineService;
import com.fanxb.bookmark.common.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/searchEngine")
public class SearchEngineController {
    @Autowired
    private SearchEngineService searchEngineService;

    /**
     * 列表查询
     */
    @GetMapping("/list")
    public Result list() {
        return Result.success(searchEngineService.list());
    }

    @PostMapping("/insert")
    public Result insert(@RequestBody SearchEngine body){
        searchEngineService.insertOne(body);
        return Result.success();
    }

    @PostMapping("/edit")
    public Result edit(@RequestBody SearchEngine body){
        searchEngineService.editOne(body);
        return Result.success();
    }

    @PostMapping("/delete")
    public Result delete(@RequestBody SearchEngine body){
        searchEngineService.deleteOne(body.getId());
        return Result.success();
    }

    @PostMapping("/setChecked")
    public Result setChecked(@RequestBody SearchEngine body){
        searchEngineService.setChecked(body.getId());
        return Result.success();
    }
}
