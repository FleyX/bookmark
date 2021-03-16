package com.fanxb.bookmark.business.bookmark.entity;


import lombok.Data;

import java.util.List;

/**
 * Description:
 *
 * @author fanxb
 * @date 2019/7/12 18:34
 */
@Data
public class BatchDeleteBody{
    /**
     * 要删除的书签路径
     */
    private List<String> pathList;
    /**
     * 要删除的书签id
     */
    private List<Integer> bookmarkIdList;
}