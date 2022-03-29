package com.fanxb.bookmark.business.bookmark.entity.vo;

import com.fanxb.bookmark.business.bookmark.entity.po.PInBookmarkPo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author fanxb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class HomePinItemVo {
    /**
     * pinBookmark Id
     */
    private Integer id;
    /**
     * 书签id
     */
    private int bookmarkId;
    /**
     * 书签名
     */
    private String name;
    /**
     * url
     */
    private String url;
    /**
     * icon
     */
    private String icon;
}
