package com.fanxb.bookmark.common.util;

import com.fanxb.bookmark.common.exception.CustomException;

import java.io.*;
import java.util.stream.Collectors;

/**
 * 功能描述: 文件相关工具类
 *
 * @author fanxb
 * @date 2019/9/26 16:45
 */
public class FileUtil {
    /**
     * 功能描述: 输入流转字符串，注意：经过此方法流会被关闭
     *
     * @param stream  输入流
     * @param charSet 编码
     * @return java.lang.String
     * @author fanxb
     * @date 2019/9/26 17:03
     */
    public static String streamToString(InputStream stream, String charSet) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charSet))
        ) {
            return reader.lines().collect(Collectors.joining());
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    /**
     * 功能描述:  使用utf-8将输入流转成字符串，经过此方法流会被关闭
     *
     * @param stream stream
     * @return java.lang.String
     * @author fanxb
     * @date 2019/9/26 17:05
     */
    public static String streamToString(InputStream stream) {
        return streamToString(stream, "utf-8");
    }

    /**
     * 功能描述:  路径
     *
     * @param path path
     * @author fanxb
     * @date 2019/11/14 0:27
     */
    public static void ensurePathExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
