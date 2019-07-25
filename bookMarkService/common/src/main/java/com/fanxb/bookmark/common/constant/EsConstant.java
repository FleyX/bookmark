package com.fanxb.bookmark.common.constant;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/23 14:34
 */
public class EsConstant {

    /**
     * 书签 index
     */
    public static final String BOOKMARK_INDEX = "bookmark";

    /**
     * 创建bookmark index语句
     */
    public static final String CREATE_BOOKMARK_INDEX = "{\n" +
            "    \"properties\": {\n" +
            "      \"userId\":{\n" +
            "        \"type\":\"integer\"\n" +
            "      },\n" +
            "      \"name\":{\n" +
            "        \"type\":\"text\",\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"search_analyzer\": \"ik_smart\"\n" +
            "      },\n" +
            "      \"url\":{\n" +
            "        \"type\":\"text\",\n" +
            "        \"index\": true,\n" +
            "        \"analyzer\": \"ik_max_word\",\n" +
            "        \"search_analyzer\": \"ik_smart\"\n" +
            "      }\n" +
            "    }\n" +
            "  }";
}
