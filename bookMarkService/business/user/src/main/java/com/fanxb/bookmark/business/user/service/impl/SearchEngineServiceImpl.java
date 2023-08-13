package com.fanxb.bookmark.business.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.support.ibatis.SpringIbatisBeanNameAutoProxyCreator;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fanxb.bookmark.business.user.dao.SearchEngineDao;
import com.fanxb.bookmark.business.user.dao.UserDao;
import com.fanxb.bookmark.business.user.entity.SearchEngine;
import com.fanxb.bookmark.business.user.service.SearchEngineService;
import com.fanxb.bookmark.common.entity.UserContext;
import com.fanxb.bookmark.common.entity.po.User;
import com.fanxb.bookmark.common.exception.CustomException;
import com.fanxb.bookmark.common.util.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SearchEngineServiceImpl implements SearchEngineService {
    @Autowired
    private SearchEngineDao searchEngineDao;
    @Autowired
    private UserDao userDao;

    @Override
    public List<SearchEngine> list() {
        return searchEngineDao.selectList(new LambdaQueryWrapper<SearchEngine>().eq(SearchEngine::getUserId, UserContextHolder.get().getUserId()));
    }

    @Override
    public void deleteOne(int id) {
        SearchEngine engine = searchEngineDao.selectById(id);
        if (engine.getUserId() != UserContextHolder.get().getUserId()) {
            throw new CustomException("无法操作其他人数据");
        }
        if (engine.getChecked() == 1) {
            throw new CustomException("默认搜索引擎无法删除");
        }
        searchEngineDao.deleteById(id);
    }

    @Override
    public void insertOne(SearchEngine body) {
        checkOne(body);
        body.setId(null).setChecked(0).setUserId(UserContextHolder.get().getUserId());
        searchEngineDao.insert(body);

    }

    private void checkOne(SearchEngine body) {
        if (StrUtil.hasBlank(body.getIcon(), body.getUrl(), body.getName())) {
            throw new CustomException("请填写完整");
        }
        if (!body.getUrl().contains("%s")) {
            throw new CustomException("路径中必须包含%s");
        }
    }

    @Override
    public void editOne(SearchEngine body) {
        SearchEngine engine = searchEngineDao.selectById(body.getId());
        if (engine.getUserId() != UserContextHolder.get().getUserId()) {
            throw new CustomException("无法操作其他人数据");
        }
        checkOne(body);
        searchEngineDao.updateById(body);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setChecked(Integer id) {
        int userId = UserContextHolder.get().getUserId();
        LambdaUpdateWrapper<SearchEngine> update = new LambdaUpdateWrapper<SearchEngine>().set(SearchEngine::getChecked, 0).eq(SearchEngine::getUserId, userId).eq(SearchEngine::getChecked, 1);
        searchEngineDao.update(null, update);
        update = new LambdaUpdateWrapper<SearchEngine>().set(SearchEngine::getChecked, 1).eq(SearchEngine::getId, id).eq(SearchEngine::getUserId, userId);
        searchEngineDao.update(null, update);
    }

    @Override
    public void newUserInit(int userId) {
        searchEngineDao.insert(new SearchEngine().setUserId(userId).setIcon("icon-baidu").setName("百度").setUrl("https://www.baidu.com/s?ie=UTF-8&wd=%s").setChecked(1));
        searchEngineDao.insert(new SearchEngine().setUserId(userId).setIcon("icon-bing").setName("必应").setUrl("https://www.bing.com/search?q=%s").setChecked(0));
        searchEngineDao.insert(new SearchEngine().setUserId(userId).setIcon("icon-google").setName("谷歌").setUrl("https://www.google.com/search?q=%s").setChecked(0));
    }
}
