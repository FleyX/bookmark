package com.fanxb.bookmark.common.util;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/4/4 16:17
 */
public class StringUtil {

    private static char[][] chars = {{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'}
            , {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'},
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'}};

    /**
     * Description: 获取随机字符串
     *
     * @param length 长度
     * @param mode   模式，0：数字，字母混合；1：字母；2：数字
     * @return java.lang.String
     * @author fanxb
     * @date 2019/4/16 13:39
     */
    public static String getRandomString(int length, int mode) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] temp = chars[mode];
        for (int i = 0; i < length; i++) {
            stringBuilder.append(temp[getRandomNumber(0, temp.length - 1)]);
        }
        return stringBuilder.toString();
    }

    /**
     * 获取指定范围的随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return
     */
    public static int getRandomNumber(int min, int max) {
        return (int) (min + Math.round(Math.random() * (max - min)));
    }

    /**
     * Description:判断字符串是否为空
     *
     * @param str 待判断字符串
     * @return boolean
     * @author fanxb
     * @date 2019/4/16 13:33
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

}
