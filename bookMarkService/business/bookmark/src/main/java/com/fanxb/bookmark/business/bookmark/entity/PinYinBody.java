package com.fanxb.bookmark.business.bookmark.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2020/3/19
 * Time: 0:05
 */
@Data
@Builder
public class PinYinBody {
    /**
     * 待转换拼音的文本列表
     */
    private List<String> strs;
    private Config config;

    @Data
    @AllArgsConstructor
    public static class Config {
        /**
         * 是否启用分词模式
         */
        private boolean segment;
        /**
         * 是否启用多音字
         */
        private boolean heteronym;
        /**
         * 风格
         */
        private int style;
    }
}
