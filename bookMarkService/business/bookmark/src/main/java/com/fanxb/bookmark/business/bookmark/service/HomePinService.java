package com.fanxb.bookmark.business.bookmark.service;

import com.fanxb.bookmark.business.bookmark.entity.po.PInBookmarkPo;
import com.fanxb.bookmark.business.bookmark.entity.vo.HomePinItemVo;

import java.util.List;

/**
 * 首页相关的书签接口
 *
 * @author fanxb
 */
public interface HomePinService {

    /**
     * 获取固定的标签
     *
     * @return {@link List<HomePinItemVo>}
     * @author fanxb
     */
    List<HomePinItemVo> getHomePinList();

    /**
     * 新增一个
     *
     * @return {@link HomePinItemVo}
     * @author fanxb
     */
    PInBookmarkPo addOne(PInBookmarkPo po);

    /**
     * 删除一个
     *
     * @param id id
     * @author fanxb
     */
    void delete(int id);

}
