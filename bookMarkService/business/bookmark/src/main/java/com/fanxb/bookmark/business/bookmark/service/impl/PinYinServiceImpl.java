package com.fanxb.bookmark.business.bookmark.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fanxb.bookmark.business.bookmark.entity.PinYinBody;
import com.fanxb.bookmark.business.bookmark.service.PinYinService;
import com.fanxb.bookmark.common.constant.Constant;
import com.fanxb.bookmark.common.util.HttpUtil;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2020/3/18
 * Time: 23:48
 */
@Service
public class PinYinServiceImpl implements PinYinService {
    private static final String PATH = "/pinyinChange";

    @Override
    public List<String> changeStrings(List<String> stringList) {
        Map<String, String> header = Collections.singletonMap("token", Constant.pinyinToken);
        PinYinBody body = PinYinBody.builder().strs(stringList).config(new PinYinBody.Config(false, false, 1)).build();
        JSONArray result =  HttpUtil.post(Constant.pinyinBaseUrl + PATH, JSON.toJSONString(body), header);
        return null;
    }
}
