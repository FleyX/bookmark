package com.fanxb.bookmark.common.entity.vo;

import lombok.Data;

/**
 * 全局公共配置
 *
 * @author fanxb
 */
@Data
public class GlobalConfigVo {
    /**
     * 是否存在网络代理(外网访问)
     */
    private boolean proxyExist;
    /**
     * bing每日一图地址
     */
    private String bingImgSrc;
}
