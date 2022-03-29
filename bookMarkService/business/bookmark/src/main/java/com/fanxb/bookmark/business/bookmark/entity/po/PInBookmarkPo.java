package com.fanxb.bookmark.business.bookmark.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 首页固定标签
 *
 * @author fanxb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("pin_bookmark")
public class PInBookmarkPo {
    @TableId(value = "id", type = IdType.AUTO)
    private int id;
    private int userId;
    private int bookmarkId;
    private int sort;
    private long createDate;
}
