package com.fanxb.bookmark.business.user.service;

import com.fanxb.bookmark.business.user.vo.OauthBody;

/**
 * @author fanxb
 * @date 2021/8/20 下午2:13
 */
public interface OauthService {
    /**
     * oauth登陆校验
     *
     * @param body body
     * @return java.lang.String
     * @author fanxb
     * @date 2021/8/20 下午2:13
     */
    String oAuthCheck(OauthBody body);
}
