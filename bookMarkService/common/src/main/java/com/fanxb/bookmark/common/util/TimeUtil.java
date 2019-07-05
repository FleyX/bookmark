package com.fanxb.bookmark.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/3/28 16:54
 */
public class TimeUtil {

    /**
     * 一天的毫秒数
     */
    public static int DAY_MS = 24 * 60 * 60 * 1000;

    private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<>();

    private static void close() {
        threadLocal.remove();
    }

    private static DateFormat getDefaultDateFormat() {
        DateFormat format = threadLocal.get();
        if (format == null) {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            threadLocal.set(format);
        }
        return format;
    }

    /**
     * Description: 格式化时间
     *
     * @param dateLong 毫秒时间戳
     * @return java.lang.String
     * @author fanxb
     * @date 2019/3/28 17:10
     */
    public static String formatTime(Long dateLong) {
        return formatTime(null, new Date(dateLong));
    }

    /**
     * Description: 格式化时间
     *
     * @param pattern  pattern
     * @param dateLong 毫秒时间戳
     * @return java.lang.String
     * @author fanxb
     * @date 2019/3/28 17:11
     */
    public static String formatTime(String pattern, Long dateLong) {
        Date date = new Date(dateLong);
        if (pattern != null) {
            return formatTime(pattern, date);
        } else {
            return formatTime(null, date);
        }
    }

    /**
     * Description: 格式化时间
     *
     * @param date 时间
     * @return java.lang.String
     * @author fanxb
     * @date 2019/3/28 17:08
     */
    public static String formatTime(Date date) {
        return formatTime(null, date);
    }


    /**
     * description:格式化时间，日期
     *
     * @param pattern 格式化字符串
     * @param date    时间
     * @return java.lang.String
     * @author fanxb
     * @date 2019/3/28 16:58
     */
    public static String formatTime(String pattern, Date date) {
        DateFormat format;
        if (pattern == null) {
            format = getDefaultDateFormat();
        } else {
            format = new SimpleDateFormat(pattern);
        }
        if (date == null) {
            return format.format(new Date());
        } else {
            return format.format(date);
        }
    }
}
