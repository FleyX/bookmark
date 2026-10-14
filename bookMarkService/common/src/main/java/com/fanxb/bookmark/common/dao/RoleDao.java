package com.fanxb.bookmark.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fanxb.bookmark.common.entity.po.RolePo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 类功能简述：角色相关dao
 *
 * @author fanxb
 */
@Component
public interface RoleDao extends BaseMapper<RolePo> {

    /**
     * Description: 查询用户拥有的启用状态角色code
     *
     * @param userId 用户id
     * @return java.util.List<java.lang.String>
     * @author fanxb
     */
    List<String> selectRoleCodesByUserId(@Param("userId") int userId);

    /**
     * Description: 查询用户拥有的启用状态角色所绑定的权限code
     *
     * @param userId 用户id
     * @return java.util.List<java.lang.String>
     * @author fanxb
     */
    List<String> selectPermissionCodesByUserId(@Param("userId") int userId);

    /**
     * Description: 根据角色code查询角色id
     *
     * @param code 角色code
     * @return java.lang.Integer
     * @author fanxb
     */
    Integer selectRoleIdByCode(@Param("code") String code);

    /**
     * Description: 绑定用户角色
     *
     * @param userId     用户id
     * @param roleId     角色id
     * @param createTime 创建时间
     * @author fanxb
     */
    void insertUserRole(@Param("userId") int userId, @Param("roleId") int roleId, @Param("createTime") long createTime);

    /**
     * Description: 查询用户绑定的全部角色（含禁用角色）
     *
     * @param userId 用户id
     * @return java.util.List<com.fanxb.bookmark.common.entity.po.RolePo>
     * @author fanxb
     */
    List<RolePo> selectRolesByUserId(@Param("userId") int userId);

    /**
     * Description: 删除用户的全部角色绑定
     *
     * @param userId 用户id
     * @author fanxb
     */
    void deleteUserRoles(@Param("userId") int userId);

    /**
     * Description: 根据角色id查询角色
     *
     * @param roleId 角色id
     * @return com.fanxb.bookmark.common.entity.po.RolePo
     * @author fanxb
     */
    RolePo selectRoleById(@Param("roleId") int roleId);

    /**
     * Description: 分页查询角色
     *
     * @param keyword 关键字，模糊匹配 code/name，可为空
     * @param start   起始行
     * @param size    页大小
     * @return java.util.List<com.fanxb.bookmark.common.entity.po.RolePo>
     * @author fanxb
     */
    List<RolePo> selectRolePage(@Param("keyword") String keyword, @Param("start") int start, @Param("size") int size);

    /**
     * Description: 统计角色数量
     *
     * @param keyword 关键字，模糊匹配 code/name，可为空
     * @return long
     * @author fanxb
     */
    long countRole(@Param("keyword") String keyword);

    /**
     * Description: 查询启用状态角色精简列表
     *
     * @return java.util.List<com.fanxb.bookmark.common.entity.po.RolePo>
     * @author fanxb
     */
    List<RolePo> selectEnabledRoles();

    /**
     * Description: 统计角色名称使用次数
     *
     * @param name          角色名称
     * @param excludeRoleId 排除的角色id，新增时传0
     * @return int
     * @author fanxb
     */
    int countRoleByName(@Param("name") String name, @Param("excludeRoleId") int excludeRoleId);

    /**
     * Description: 新增角色
     *
     * @param role 角色
     * @author fanxb
     */
    void insertRole(RolePo role);

    /**
     * Description: 更新角色名称与描述
     *
     * @param roleId      角色id
     * @param name        角色名称
     * @param description 描述
     * @param updateTime  更新时间
     * @author fanxb
     */
    void updateRole(@Param("roleId") int roleId, @Param("name") String name, @Param("description") String description,
                    @Param("updateTime") long updateTime);

    /**
     * Description: 更新角色状态
     *
     * @param roleId     角色id
     * @param status     1启用 0禁用
     * @param updateTime 更新时间
     * @author fanxb
     */
    void updateRoleStatus(@Param("roleId") int roleId, @Param("status") int status, @Param("updateTime") long updateTime);

    /**
     * Description: 删除角色
     *
     * @param roleId 角色id
     * @author fanxb
     */
    void deleteRole(@Param("roleId") int roleId);

    /**
     * Description: 查询角色绑定的权限code
     *
     * @param roleId 角色id
     * @return java.util.List<java.lang.String>
     * @author fanxb
     */
    List<String> selectPermissionCodesByRoleId(@Param("roleId") int roleId);

    /**
     * Description: 统计绑定该角色的用户数量
     *
     * @param roleId 角色id
     * @return int
     * @author fanxb
     */
    int countUserByRoleId(@Param("roleId") int roleId);

    /**
     * Description: 删除角色的全部权限绑定
     *
     * @param roleId 角色id
     * @author fanxb
     */
    void deleteRolePermissions(@Param("roleId") int roleId);

    /**
     * Description: 新增角色权限绑定
     *
     * @param roleId         角色id
     * @param permissionCode 权限code
     * @param createTime     创建时间
     * @author fanxb
     */
    void insertRolePermission(@Param("roleId") int roleId, @Param("permissionCode") String permissionCode,
                              @Param("createTime") long createTime);

    /**
     * Description: 根据角色code查询角色id并加行锁（须在事务内调用），用于首个用户绑超管的并发安全
     *
     * @param code 角色code
     * @return java.lang.Integer
     * @author fanxb
     */
    Integer selectRoleIdByCodeForUpdate(@Param("code") String code);

    /**
     * Description: 查询全部已绑定的权限code（去重），用于启动时悬空绑定扫描
     *
     * @return java.util.List<java.lang.String>
     * @author fanxb
     */
    List<String> selectDistinctPermissionCodes();
}
