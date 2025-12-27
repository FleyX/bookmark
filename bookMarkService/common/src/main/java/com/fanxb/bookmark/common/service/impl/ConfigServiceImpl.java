package com.fanxb.bookmark.common.service.impl;

import cn.hutool.core.io.FileUtil;
import com.fanxb.bookmark.common.constant.CommonConstant;
import com.fanxb.bookmark.common.constant.RedisConstant;
import com.fanxb.bookmark.common.dao.GlobalConfigDao;
import com.fanxb.bookmark.common.entity.po.GlobalConfigPo;
import com.fanxb.bookmark.common.entity.vo.GlobalConfigVo;
import com.fanxb.bookmark.common.service.ConfigService;
import com.fanxb.bookmark.common.util.HttpUtil;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    @PostConstruct
    public void init() {
        //初始化bing 图
        getCacheBingImg();
    }

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
        stringRedisTemplate.opsForValue().set(RedisConstant.BING_IMG, str, 2, TimeUnit.HOURS);
        return str;
    }

    private String getBingImg() {
        try {
            log.info("[getBingImg]start");
            ObjectNode bingObj = HttpUtil.getObj(bingHost + bingUrl, null, false);
            String path = bingObj.get("images").get(0).get("url").asText();
            String picUrl = bingHost + path;
            Request request = new Request.Builder().url(picUrl).build();
            try (Response res = HttpUtil.getClient(false).newCall(request).execute()) {
                byte[] bytes = res.body().bytes();
                String filePath = CommonConstant.fileSavePath + "/files/public/bing.jpg";
                File file = FileUtil.writeBytes(bytes, filePath);
                log.info(file.getAbsolutePath());
            } catch (Exception e) {
                log.error("获取bing每日一图错误：{}", e.getLocalizedMessage(), e);
            }
        } catch (Exception e) {
            log.error("获取bing每日一图错误：{}", e.getLocalizedMessage(), e);
        }
        return "/files/public/bing.jpg";
    }


}
