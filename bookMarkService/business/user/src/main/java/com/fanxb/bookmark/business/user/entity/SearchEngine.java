package com.fanxb.bookmark.business.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("search_engine")
public class SearchEngine {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private Integer checked;
    /**
     * 名称
     */
    private String name;
    /**
     * url
     */
    private String url;
    /**
     * 图标
     */
    private String icon;
}
