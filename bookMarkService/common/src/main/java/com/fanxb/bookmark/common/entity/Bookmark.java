package com.fanxb.bookmark.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 类功能简述：书签树
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/8 11:19
 */
@Data
public class Bookmark {
    /**
     * 书签类别
     */
    public static final int BOOKMARK_TYPE = 0;
    /**
     * 文件夹类别
     */
    public static final int FOLDER_TYPE = 1;

    @TableId(type = IdType.AUTO)
    private Integer bookmarkId;
    /**
     * 类型：1：文件夹，0：具体的书签
     */
    private Integer type;
    private Integer userId;
    private String path;
    private String name;
    private String url = "";
    private String icon = "";
    private Integer sort;
    private String searchKey = "";
    private Long addTime;
    private Long createTime;
    /**
     * 访问次数
     */
    private int visitNum;
    private List<Bookmark> children;

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
        this.setChildren(new ArrayList<>());
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
