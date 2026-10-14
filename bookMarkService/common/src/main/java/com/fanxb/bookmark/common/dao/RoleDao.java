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
}
