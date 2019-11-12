package com.fanxb.bookmark.business.bookmark.dao;

import com.fanxb.bookmark.common.entity.Bookmark;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2019/11/12
 * Time: 0:24
 */
@Mapper
public interface BookmarkBackupDao {

    /**
     * 分页获取所有的书签
     * @param size 大小
     * @param startIndex 开始下标
     * @return
     */
    @Select("select * from bookmark order by bookmarkId limit ${startIndex},${size}")
    List<Bookmark> getBookmarkListPage(@Param("size") int size,@Param("startIndex") int startIndex);
}
