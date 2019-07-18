package com.fanxb.bookmark.business.bookmark.entity;

import lombok.Data;

/**
 * 类功能简述： 移动节点表单
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/18 10:04
 */
@Data
public class MoveNodeBody {
    int bookmarkId;
    String sourcePath;
    String targetPath;
    /**
     * sort -1，表示移动到某个文件夹下，放到最后
     */
    int sort;
}
