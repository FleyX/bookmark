package com.fanxb.bookmark.business.bookmark.dao;

import com.fanxb.bookmark.common.entity.Bookmark;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/8 16:39
 */
@Component
public interface BookmarkDao {

    /**
     * Description: 插入一条书签记录
     *
     * @param node node
     * @return void
     * @author fanxb
     * @date 2019/7/8 16:49
     */
    void insertOne(Bookmark node);

    /**
     * Description: 根据用户名和name获取节点id
     *
     * @param userId 用户名
     * @param name   姓名
     * @param path   当前节点路径
     * @return Integer
     * @author fanxb
     * @date 2019/7/8 17:18
     */
    Integer selectIdByUserIdAndNameAndPath(@Param("userId") int userId, @Param("name") String name, @Param("path") String path);

    /**
     * Description:
     *
     * @param userId 用户id
     * @param path   父节点路径
     * @return java.lang.Integer
     * @author fanxb
     * @date 2019/7/8 17:35
     */
    Integer selectMaxSort(@Param("userId") int userId, @Param("path") String path);
}
