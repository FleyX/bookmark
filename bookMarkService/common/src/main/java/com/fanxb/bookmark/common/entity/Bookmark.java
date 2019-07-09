package com.fanxb.bookmark.common.entity;

import lombok.Data;

import java.util.ArrayList;

/**
 * 类功能简述：书签树
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/8 11:19
 */
@Data
public class Bookmark {
    public static final int BOOKMARK_TYPE = 0;
    public static final int FOLDER_TYPE = 1;

    private Integer bookmarkId;
    /**
     * 类型：0：文件夹，1：具体的书签
     */
    private int type;
    private int userId;
    private String path;
    private String name;
    private String url;
    private String icon;
    private int sort;
    private long addTime;
    private long createTime;
    private ArrayList<Bookmark> children;

    public Bookmark() {
    }

    public Bookmark(int userId, String path, String name, long addTime, int sort) {
        this.setUserId(userId);
        this.setPath(path);
        this.setType(FOLDER_TYPE);
        this.setName(name);
        this.setAddTime(addTime);
        this.setSort(sort);
        this.setCreateTime(System.currentTimeMillis());
    }

    public Bookmark(int userId, String path, String name, String url, String icon, long addTime, int sort) {
        this.setUserId(userId);
        this.setPath(path);
        this.setType(BOOKMARK_TYPE);
        this.setName(name);
        this.setUrl(url);
        this.setIcon(icon);
        this.setSort(sort);
        this.setAddTime(addTime);
        this.setCreateTime(System.currentTimeMillis());
    }
}
