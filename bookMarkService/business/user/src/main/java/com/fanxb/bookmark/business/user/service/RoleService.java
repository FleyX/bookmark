package com.fanxb.bookmark.business.user.service;

import com.fanxb.bookmark.business.user.vo.RoleAddBody;
import com.fanxb.bookmark.business.user.vo.RoleDetailRes;
import com.fanxb.bookmark.business.user.vo.RoleOptionRes;
import com.fanxb.bookmark.business.user.vo.RolePageRes;
import com.fanxb.bookmark.business.user.vo.RoleUpdateBody;
import com.fanxb.bookmark.common.permission.Permission;

import java.util.List;

/**
 * 类功能简述：角色管理服务
 *
 * @author fanxb
 */
public interface RoleService {

    /**
     * Description: 分页查询角色
     *
     * @param pageNum  页码，从1开始
     * @param pageSize 页大小
     * @param keyword  关键字，模糊匹配 code/name，可为空
     * @return com.fanxb.bookmark.business.user.vo.RolePageRes
     * @author fanxb
     */
    RolePageRes page(int pageNum, int pageSize, String keyword);

    /**
     * Description: 角色详情，含已绑权限code
     *
     * @param roleId 角色id
     * @return com.fanxb.bookmark.business.user.vo.RoleDetailRes
     * @author fanxb
     */
    RoleDetailRes detail(int roleId);

    /**
     * Description: 启用状态角色精简列表
     *
     * @return java.util.List<com.fanxb.bookmark.business.user.vo.RoleOptionRes>
     * @author fanxb
     */
    List<RoleOptionRes> options();

    /**
     * Description: 权限树，数据源为内存注册表
     *
     * @return java.util.List<com.fanxb.bookmark.common.permission.Permission>
     * @author fanxb
     */
    List<Permission> permissionTree();

    /**
     * Description: 新建角色
     *
     * @param body 表单
     * @author fanxb
     */
    void add(RoleAddBody body);

    /**
     * Description: 编辑角色，code 不可修改
     *
     * @param body 表单
     * @author fanxb
     */
    void update(RoleUpdateBody body);

    /**
     * Description: 启用/禁用角色
     *
     * @param roleId 角色id
     * @param status 1启用 0禁用
     * @author fanxb
     */
    void updateStatus(int roleId, int status);

    /**
     * Description: 删除角色，有用户绑定时拒绝
     *
     * @param roleId 角色id
     * @author fanxb
     */
    void delete(int roleId);

    /**
     * Description: 绑定权限，全量替换
     *
     * @param roleId          角色id
     * @param permissionCodes 权限code列表
     * @author fanxb
     */
    void bindPermissions(int roleId, List<String> permissionCodes);
}
