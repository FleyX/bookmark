package com.fanxb.bookmark.common.service;

import java.util.Set;

/**
 * 类功能简述：用户权限解析服务
 *
 * @author fanxb
 */
public interface PermissionService {

    /**
     * Description: 是否超级管理员
     *
     * @param userId 用户id
     * @return boolean
     * @author fanxb
     */
    boolean isSuperAdmin(int userId);

    /**
     * Description: 解析用户拥有的资源code集合
     *
     * @param userId 用户id
     * @return java.util.Set<java.lang.String>
     * @author fanxb
     */
    Set<String> resolveResourceCodes(int userId);

    /**
     * Description: 失效指定用户的权限缓存
     *
     * @param userId 用户id
     * @author fanxb
     */
    void evictUser(int userId);

    /**
     * Description: 失效全部用户的权限缓存
     *
     * @author fanxb
     */
    void evictAll();
}
