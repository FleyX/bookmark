package com.fanxb.bookmark.common.util;

import com.alibaba.fastjson.JSONObject;
import com.fanxb.bookmark.common.exception.CustomException;
import okhttp3.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/4/4 15:53
 */
public class HttpUtil {
    private static final int IP_LENGTH = 15;

    public static final OkHttpClient CLIENT = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS).build();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static JSONObject get(String url) {
        return get(url, null);
    }

    public static JSONObject get(String url, Map<String, String> headers) {
        Request.Builder builder = new Request.Builder().url(url);
        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                builder = builder.addHeader(key, headers.get(key));
            }
        }
        return request(builder.build());
    }

    public static JSONObject post(String url, String jsonObj) {
        return post(url, jsonObj, null);
    }

    /**
     * 发送post请求，带header
     *
     * @param url     url
     * @param jsonObj 请求体
     * @param headers 请求头
     * @return
     */
    public static JSONObject post(String url, String jsonObj, Map<String, String> headers) {
        RequestBody body = RequestBody.create(JSON, jsonObj);
        Request.Builder builder = new Request.Builder().url(url).post(body);
        if (headers != null) {
            Set<String> set = headers.keySet();
            for (String key : set) {
                builder = builder.addHeader(key, headers.get(key));
            }
        }
        return request(builder.build());
    }

    /**
     * 构造request，获取响应
     *
     * @param request request
     * @return
     */
    public static JSONObject request(Request request) {
        try (Response res = CLIENT.newCall(request).execute()) {
            return parseResponse(res);
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    /**
     * 解析响应
     *
     * @param res
     * @return
     */
    public static JSONObject parseResponse(Response res) {
        try {
            if (checkIsOk(res.code())) {
                String str = res.body().string();
                return JSONObject.parseObject(str);
            } else {
                throw new CustomException("http请求出错:" + res.body().string());
            }
        } catch (Exception e) {
            throw new CustomException(e);
        }
    }

    public static boolean checkIsOk(int code) {
        return code >= 200 && code < 300;
    }

    /**
     * Description: 从请求头中获取用户访问ip
     *
     * @param request 请求头
     * @return java.lang.String
     * @author fanxb
     * @date 2019/6/12 15:36
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0) {
                ipAddress = request.getRemoteAddr();
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > IP_LENGTH) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }
}


