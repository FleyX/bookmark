package com.fanxb.bookmark.common.service;

import com.fanxb.bookmark.common.entity.vo.GlobalConfigVo;

/**
 * 全局配置相关
 *
 * @author fanxb
 * @date 2021-09-15-下午9:56
 */
public interface ConfigService {

    /**
     * 获取全局配置,用户无关，是否登陆都能获取
     *
     * @return java.util.Map<java.lang.String, java.lang.String>
     * @author fanxb
     * @date 2021/9/15 下午9:58
     */
    GlobalConfigVo getGlobalConfig();

    /**
     * 缓存bing每日一图到redis
     *
     * @author fanxb
     */
    String getCacheBingImg();
}
