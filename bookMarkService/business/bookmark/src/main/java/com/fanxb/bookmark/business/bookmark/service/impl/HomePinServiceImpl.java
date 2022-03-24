package com.fanxb.bookmark.business.bookmark.service.impl;

import com.fanxb.bookmark.business.bookmark.dao.BookmarkDao;
import com.fanxb.bookmark.business.bookmark.dao.PinBookmarkDao;
import com.fanxb.bookmark.business.bookmark.entity.po.PInBookmarkPo;
import com.fanxb.bookmark.business.bookmark.entity.vo.HomePinItemVo;
import com.fanxb.bookmark.business.bookmark.service.BookmarkService;
import com.fanxb.bookmark.business.bookmark.service.HomePinService;
import com.fanxb.bookmark.common.entity.UserContext;
import com.fanxb.bookmark.common.util.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fanxb
 */
@Service
@Slf4j
public class HomePinServiceImpl implements HomePinService {

    private final PinBookmarkDao pinBookmarkDao;
    private final BookmarkDao bookmarkDao;

    @Autowired
    public HomePinServiceImpl(PinBookmarkDao pinBookmarkDao, BookmarkDao bookmarkDao) {
        this.pinBookmarkDao = pinBookmarkDao;
        this.bookmarkDao = bookmarkDao;
    }

    /**
     * 首页固定书签最大数量
     */
    private static final int MAX_PIN = 20;


    @Override
    public List<HomePinItemVo> getHomePinList() {
        List<HomePinItemVo> res = new ArrayList<>(MAX_PIN);
        if (UserContextHolder.get() != null) {
            res.addAll(pinBookmarkDao.selectUserPin(UserContextHolder.get().getUserId()));
            if (res.size() < MAX_PIN) {
                res.addAll(bookmarkDao.selectPopular(UserContextHolder.get().getUserId(), MAX_PIN - res.size()).stream()
                        .map(item -> new HomePinItemVo(null, item.getBookmarkId(), item.getName(), item.getUrl(), item.getIcon())).collect(Collectors.toList()));
            }
        }
        return res;
    }

    @Override
    public PInBookmarkPo addOne(PInBookmarkPo po) {
        int userId = UserContextHolder.get().getUserId();
        po.setUserId(userId);
        po.setCreateDate(System.currentTimeMillis());
        po.setSort(pinBookmarkDao.getUserMaxSort(userId) + 1);
        pinBookmarkDao.insert(po);
        return po;
    }

    @Override
    public void delete(int id) {
        pinBookmarkDao.deleteById(id);
    }

}
