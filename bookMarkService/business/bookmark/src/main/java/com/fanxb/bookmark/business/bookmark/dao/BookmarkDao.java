package com.fanxb.bookmark.business.bookmark.dao;

import com.fanxb.bookmark.common.entity.Bookmark;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

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

    /**
     * Description: 根据用户id获取其所有数据
     *
     * @param userId userid
     * @return java.util.List<com.fanxb.bookmark.common.entity.Bookmark>
     * @author fanxb
     * @date 2019/7/9 18:55
     */
    List<Bookmark> getListByUserId(int userId);

    /**
     * Description: 根据userId和path查询path下的子节点
     *
     * @param userId userId
     * @param path   path
     * @return java.util.List<com.fanxb.bookmark.common.entity.Bookmark>
     * @author fanxb
     * @date 2019/7/17 14:48
     */
    List<Bookmark> getListByUserIdAndPath(@Param("userId") int userId, @Param("path") String path);

    /**
     * Description: 删除某用户某个书签文件下所有数据
     *
     * @param userId   用户id
     * @param folderId 文件夹id
     * @author fanxb
     * @date 2019/7/12 14:13
     */
    void deleteUserFolder(@Param("userId") int userId, @Param("folderId") int folderId);

    /**
     * Description: 删除用户书签
     *
     * @param userId      用户id
     * @param bookmarkIds 书签id
     * @author fanxb
     * @date 2019/7/12 17:24
     */
    void deleteUserBookmark(@Param("userId") int userId, @Param("bookmarkIds") List<Integer> bookmarkIds);

    /**
     * Description: 编辑书签
     *
     * @param bookmark bookmark
     * @author fanxb
     * @date 2019/7/17 14:49
     */
    void editBookmark(Bookmark bookmark);

    /**
     * Description: path下sort>=某个值的sort自增1
     *
     * @param userId userId
     * @param path   path
     * @param sort   sort
     * @author fanxb
     * @date 2019/7/18 10:13
     */
    void sortPlus(@Param("userId") int userId, @Param("path") String path, @Param("sort") int sort);

    /**
     * Description: 更新某个路径下所有子节点的路径信息
     *
     * @param userId  userId
     * @param oldPath oldPath
     * @param newPath newPath
     * @author fanxb
     * @date 2019/7/18 10:35
     */
    void updateChildrenPath(@Param("userId") int userId, @Param("oldPath") String oldPath, @Param("newPath") String newPath);

    /**
     * Description: 更新某个书签的path，sort
     *
     * @param userId     userId
     * @param bookmarkId bookmarkId
     * @param path       path
     * @param sort       sort
     * @author fanxb
     * @date 2019/7/18 10:37
     */
    void updatePathAndSort(@Param("userId") int userId, @Param("bookmarkId") int bookmarkId, @Param("path") String path, @Param("sort") int sort);


}
