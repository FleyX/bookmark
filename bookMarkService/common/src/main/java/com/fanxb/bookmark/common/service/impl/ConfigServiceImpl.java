package com.fanxb.bookmark.common.service.impl;

import com.fanxb.bookmark.common.service.ConfigService;
import com.fanxb.bookmark.common.util.HttpUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fanxb
 * @date 2021-09-15-下午9:59
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Override
    public Map<String, Object> getGlobalConfig() {
        Map<String, Object> res = new HashMap<>(1);
        res.put("proxyExist", HttpUtil.getProxyExist());
        return res;
    }
}
