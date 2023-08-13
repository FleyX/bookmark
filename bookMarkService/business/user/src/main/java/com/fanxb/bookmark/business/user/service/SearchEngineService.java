package com.fanxb.bookmark.business.user.service;

import com.fanxb.bookmark.business.user.entity.SearchEngine;

import java.util.List;

public interface SearchEngineService {

    /**
     * 列表查询
     */
    List<SearchEngine> list();

    /**
     * delete one by id
     *
     * @param id id
     */
    void deleteOne(int id);

    /**
     * insert one
     *
     * @param body body
     */
    void insertOne(SearchEngine body);


    /**
     * edit one
     *
     * @param body body
     */
    void editOne(SearchEngine body);

    /**
     * 设为默认搜索项
     *
     * @param id
     */
    void setChecked(Integer id);

    /**
     * 新用户初始化
     *
     * @param userId userId
     */
    void newUserInit(int userId);
}
