package com.fanxb.bookmark.common.service;

import java.util.Date;

/**
 * 类功能简述：用户状态服务，禁用/重置密码后的 JWT 吊销判断
 *
 * @author fanxb
 */
public interface UserStateService {

    /**
     * Description: 校验用户当前状态是否可用
     *
     * @param userId     用户id
     * @param jwtIssuedAt jwt 签发时间，旧 token 为 null
     * @return boolean true 可用，false 应按未登录处理
     * @author fanxb
     */
    boolean checkUserState(int userId, Date jwtIssuedAt);

    /**
     * Description: 禁用用户
     *
     * @param userId 用户id
     * @author fanxb
     */
    void disable(int userId);

    /**
     * Description: 启用用户
     *
     * @param userId 用户id
     * @author fanxb
     */
    void enable(int userId);

    /**
     * Description: 记录密码被管理员重置
     *
     * @param userId 用户id
     * @author fanxb
     */
    void markPasswordReset(int userId);

    /**
     * Description: 失效指定用户的状态缓存
     *
     * @param userId 用户id
     * @author fanxb
     */
    void evict(int userId);
}
