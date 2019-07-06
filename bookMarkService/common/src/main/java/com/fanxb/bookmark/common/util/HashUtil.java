package com.fanxb.bookmark.common.util;

import com.fanxb.bookmark.common.exception.CustomException;

import java.security.MessageDigest;

/**
 * 类功能简述：消息摘要计算
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/6 14:31
 */
public class HashUtil {

    /**
     * Description: md5 签名字符串
     *
     * @param str 16进制字符串
     * @return java.lang.String
     * @author fanxb
     * @date 2019/7/6 14:38
     */
    public static String md5(String str) {
        return hash(str, "MD5");
    }

    /**
     * Description: 使用sha1签名字符串
     *
     * @param str sha1签名字符串
     * @return java.lang.String
     * @author fanxb
     * @date 2019/7/6 14:40
     */
    public static String sha1(String str) {
        return hash(str, "SHA1");
    }

    /**
     * Description: 根据type签名str
     *
     * @param str  str
     * @param type 签名类别
     * @return java.lang.String
     * @author fanxb
     * @date 2019/7/6 14:40
     */
    public static String hash(String str, String type) {
        try {
            MessageDigest md = MessageDigest.getInstance(type);
            md.update(str.getBytes());
            return bytesToHexString(md.digest());
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }


    /**
     * Description: 字节数组转16进制字符串
     *
     * @param bits 字节数组
     * @return java.lang.String
     * @author fanxb
     * @date 2019/7/6 14:37
     */
    public static String bytesToHexString(byte[] bits) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, length = bits.length; i < length; i++) {
            int k = bits[i];
            if (k < 0) {
                k += 256;
            }
            if (k < 16) {
                builder.append("0");
            }
            builder.append(Integer.toHexString(k));
        }
        return builder.toString();
    }

    public static void main(String[] args){
        System.out.println(md5("abc"));
        System.out.println(sha1("abc"));
    }

}
