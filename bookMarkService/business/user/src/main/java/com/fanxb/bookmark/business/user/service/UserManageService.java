package com.fanxb.bookmark.business.user.service;

import com.fanxb.bookmark.business.user.vo.UserDetailRes;
import com.fanxb.bookmark.business.user.vo.UserPageRes;

import java.util.List;

/**
 * 类功能简述：用户管理服务
 *
 * @author fanxb
 */
public interface UserManageService {

    /**
     * Description: 分页查询用户，keyword 模糊匹配 username/email
     *
     * @param pageNum  页码，从1开始
     * @param pageSize 页大小
     * @param keyword  关键字，可为空
     * @return com.fanxb.bookmark.business.user.vo.UserPageRes
     * @author fanxb
     */
    UserPageRes page(int pageNum, int pageSize, String keyword);

    /**
     * Description: 用户详情，含书签数与已绑角色
     *
     * @param userId 用户id
     * @return com.fanxb.bookmark.business.user.vo.UserDetailRes
     * @author fanxb
     */
    UserDetailRes detail(int userId);

    /**
     * Description: 启用/禁用用户，禁用后旧 JWT 失效
     *
     * @param userId 用户id
     * @param status 1正常 0禁用
     * @author fanxb
     */
    void updateStatus(int userId, int status);

    /**
     * Description: 分配角色，全量替换
     *
     * @param userId  用户id
     * @param roleIds 角色id列表
     * @author fanxb
     */
    void assignRoles(int userId, List<Integer> roleIds);

    /**
     * Description: 重置用户密码，重置后旧 JWT 失效
     *
     * @param userId   用户id
     * @param password 新密码明文
     * @author fanxb
     */
    void resetPassword(int userId, String password);
}
