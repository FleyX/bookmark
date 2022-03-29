package com.fanxb.bookmark.business.bookmark.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fanxb.bookmark.business.bookmark.entity.BookmarkEs;
import com.fanxb.bookmark.business.bookmark.entity.redis.VisitNumPlus;
import com.fanxb.bookmark.common.entity.po.Bookmark;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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
public interface BookmarkDao extends BaseMapper<Bookmark> {

    /**
     * Description: 插入一条书签记录
     *
     * @param node node
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
     * @return java.util.List<com.fanxb.bookmark.common.entity.po.Bookmark>
     * @author fanxb
     * @date 2019/7/9 18:55
     */
    List<Bookmark> getListByUserId(int userId);

    /**
     * Description: 根据userId和path查询path下的子节点
     *
     * @param userId userId
     * @param path   path
     * @return java.util.List<com.fanxb.bookmark.common.entity.po.Bookmark>
     * @author fanxb
     * @date 2019/7/17 14:48
     */
    List<Bookmark> getListByUserIdAndPath(@Param("userId") int userId, @Param("path") String path);

    /**
     * Description: 删除某用户某个书签路径下所有数据,不包含文件夹自身
     *
     * @param userId 用户id
     * @param path   文件夹id
     * @author fanxb
     * @date 2019/7/12 14:13
     */
    void deleteUserFolder(@Param("userId") int userId, @Param("path") String path);

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

    /**
     * Description: 获取某个路径下所有的节点id
     *
     * @param userId userId
     * @param path   path
     * @return java.util.List<java.lang.Integer>
     * @author fanxb
     * @date 2019/7/25 14:14
     */
    List<Integer> getChildrenBookmarkId(@Param("userId") int userId, @Param("path") String path);

    /**
     * Description: 根据用户id，类别，分页查找书签
     *
     * @param userId userId
     * @param type   type
     * @param start  start
     * @param size   size
     * @return java.util.List<com.fanxb.bookmark.business.bookmark.entity.BookmarkEs>
     * @author fanxb
     * @date 2019/7/26 15:23
     */
    List<BookmarkEs> selectBookmarkEsByUserIdAndType(@Param("userId") int userId, @Param("type") int type, @Param("start") int start, @Param("size") int size);

    /**
     * 功能描述: 查询所有的bookmark,用于全量更新拼音key
     *
     * @param bookmarkId bookmarkId
     * @param size       size
     * @return List<Bookmark>
     * @author fanxb
     * @date 2020/3/22 22:06
     */
    @Select("select bookmarkId,name,url from bookmark where bookmarkId>${bookmarkId} order by bookmarkId asc limit 0,${size}")
    List<Bookmark> selectPinyinEmpty(@Param("bookmarkId") int bookmarkId, @Param("size") int size);

    /**
     * 功能描述: 更新一个bookmark的key
     *
     * @param bookmarkId id
     * @param searchKey  searchKey
     * @author fanxb
     * @date 2020/3/22 22:08
     */
    @Update("update bookmark set searchKey=#{searchKey} where bookmarkId=#{bookmarkId}")
    void updateSearchKey(@Param("bookmarkId") int bookmarkId, @Param("searchKey") String searchKey);

    /**
     * 批量更新searchKey
     *
     * @param list list
     * @author fanxb
     * @date 2020/3/22 22:08
     */
    void updateSearchKeyBatch(List<Bookmark> list);

    /**
     * 分页获取所有的书签
     *
     * @param size       大小
     * @param startIndex 开始下标
     * @return bookmark List
     * @author fanxb
     */
    @Select("select * from bookmark order by bookmarkId limit ${startIndex},${size}")
    List<Bookmark> getBookmarkListPage(@Param("size") int size, @Param("startIndex") int startIndex);

    /**
     * 功能描述: 书签访问次数+1
     *
     * @param item 信息
     * @author fanxb
     * @date 2020/5/12 10:40
     */
    @Update("update bookmark set visitNum=visitNum+1 where userId=#{userId} and bookmarkId=#{bookmarkId}")
    void updateVisitNum(VisitNumPlus item);

    /**
     * 查询用户最常访问的几个书签
     *
     * @param userId 用户id
     * @param num    num
     * @return java.util.List<com.fanxb.bookmark.common.entity.po.Bookmark>
     * @author fanxb
     * @date 2021/8/20 上午11:52
     */
    @Select("select bookmarkId,name,url,icon from bookmark where userId=#{userId} and type=0 order by visitNum desc limit 0,#{num}")
    List<Bookmark> selectPopular(@Param("userId") int userId, @Param("num") int num);

    /**
     * 获取某用户无icon的条目
     *
     * @param userId userId
     * @param start  开始
     * @param size   数量
     * @return java.util.List<com.fanxb.bookmark.common.entity.po.Bookmark>
     * @author fanxb
     * @date 2021/8/20 下午12:02
     */
    @Select("select userId,bookmarkId,url,icon from bookmark where userId=#{userId} and icon='' order by bookmarkId asc limit #{start},#{size}")
    List<Bookmark> selectUserNoIcon(@Param("userId") int userId, @Param("start") int start, @Param("size") int size);

    /**
     * 更新icon
     *
     * @param bookmarkId bookmarkId
     * @param icon       icon
     * @author fanxb
     * @date 2021/3/13
     **/
    @Update("update bookmark set icon=#{icon} where bookmarkId=#{bookmarkId}")
    void updateIcon(@Param("bookmarkId") int bookmarkId, @Param("icon") String icon);

    /**
     * 查询一个用户全部的书签路径
     *
     * @param userId userId
     * @return java.util.List<com.fanxb.bookmark.common.entity.po.Bookmark>
     * @author fanxb
     * @date 2021/8/20 上午11:58
     */
    @Select("select bookmarkId,path from bookmark where userId=#{userId}")
    List<Bookmark> selectBookmarkIdPathByUserId(int userId);

}
