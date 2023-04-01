package com.fanxb.bookmark.common.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.fanxb.bookmark.common.constant.NumberConstant;
import com.fanxb.bookmark.common.constant.RedisConstant;
import com.fanxb.bookmark.common.dao.GlobalConfigDao;
import com.fanxb.bookmark.common.entity.po.GlobalConfigPo;
import com.fanxb.bookmark.common.entity.vo.GlobalConfigVo;
import com.fanxb.bookmark.common.service.ConfigService;
import com.fanxb.bookmark.common.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author fanxb
 * @date 2021-09-15-下午9:59
 */
@Service
@Slf4j
public class ConfigServiceImpl implements ConfigService {


    private final StringRedisTemplate stringRedisTemplate;
    private final GlobalConfigDao globalConfigDao;

    @Autowired
    public ConfigServiceImpl(StringRedisTemplate stringRedisTemplate, GlobalConfigDao globalConfigDao) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.globalConfigDao = globalConfigDao;
    }

    @Value("${bing.host}")
    private String bingHost;
    @Value("${bing.onePic}")
    private String bingUrl;

    @Override
    public GlobalConfigVo getGlobalConfig() {
        List<GlobalConfigPo> pos = globalConfigDao.selectByMap(Collections.emptyMap());
        Map<String, String> map = pos.stream().collect(Collectors.toMap(GlobalConfigPo::getCode, GlobalConfigPo::getValue));
        GlobalConfigVo vo = new GlobalConfigVo();
        vo.setBingImgSrc(getCacheBingImg());
        vo.setMap(map);
        return vo;
    }

    @Override
    public String getCacheBingImg() {
        String str = stringRedisTemplate.opsForValue().get(RedisConstant.BING_IMG);
        if (str != null) {
            return str;
        }
        str = getBingImg();
        if (str != null) {
            stringRedisTemplate.opsForValue().set(RedisConstant.BING_IMG, str, 2, TimeUnit.HOURS);
        }
        return str;
    }

    private String getBingImg() {
        try {
            JSONObject bingObj = HttpUtil.getObj(bingHost + bingUrl, null, false);
            String path = bingObj.getJSONArray("images").getJSONObject(0).getString("url");
            return bingHost + path;
        } catch (Exception e) {
            log.error("获取bing每日一图错误：{}", e.getLocalizedMessage(), e);
        }
        return null;
    }


}
