package com.fanxb.bookmark.business.bookmark.service;

import java.util.List;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2020/3/18
 * Time: 23:47
 */
public interface PinYinService {

    /**
     * 分隔符
     */
    static final String PARTITION = "||";
    /**
     * 拼音接口路径
     */
    static final String PATH = "/pinyinChange";
    /**
     * 分页查询页大小
     */
    static final int SIZE = 500;

    /**
     * 功能描述: 首次上线用于全量初始化
     *
     * @author fanxb
     * @date 2020/3/22 21:40
     */
    void changeAll();

    /**
     * 功能描述:返回用于前端搜索的key
     *
     * @param str 待拼音化的字符串
     * @return java.util.List<java.lang.String>
     * @author fanxb
     * @date 2020/3/22 21:38
     */
    String changeString(String str);

    /**
     * 功能描述:返回用于前端搜索的key
     *
     * @param stringList 待拼音化的字符串
     * @return java.util.List<java.lang.String>
     * @author fanxb
     * @date 2020/3/22 21:38
     */
    List<String> changeStrings(List<String> stringList);
}
