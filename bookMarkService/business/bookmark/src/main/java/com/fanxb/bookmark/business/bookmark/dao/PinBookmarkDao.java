package com.fanxb.bookmark.business.bookmark.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fanxb.bookmark.business.bookmark.entity.po.PInBookmarkPo;
import com.fanxb.bookmark.business.bookmark.entity.vo.HomePinItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author fanxb
 */
@Mapper
public interface PinBookmarkDao extends BaseMapper<PInBookmarkPo> {

    /***
     * 获取用户固定的书签
     *
     * @param userId userId
     *  @return {@link List<HomePinItemVo>}
     * @author fanxb
     */
    @Select("select a.id,b.bookmarkId,b.name,b.url,b.icon from pin_bookmark a inner join bookmark b on a.bookmarkId=b.bookmarkId where a.userId=#{userId}")
    List<HomePinItemVo> selectUserPin(int userId);

    /**
     * 获取当前最大的序列号
     *
     * @param userId userId
     * @return {@link int}
     * @author fanxb
     */
    @Select("select ifnull(max(sort),0) from pin_bookmark where userId=#{userId}")
    int getUserMaxSort(int userId);
}
