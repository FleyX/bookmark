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
    private List<Integer> folderIdList;
    private List<Integer> bookmarkIdList;
}