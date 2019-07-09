package com.fanxb.bookmark.common.dao;

import com.fanxb.bookmark.common.entity.Url;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/9 14:47
 */
@Component
public interface UrlDao {

    /**
     * Description: 获取公共接口
     *
     * @author fanxb
     * @date 2019/7/9 14:52
     * @return java.util.List<com.fanxb.bookmark.common.entity.Url>
     */
    List<Url> getPublicUrl();
}
