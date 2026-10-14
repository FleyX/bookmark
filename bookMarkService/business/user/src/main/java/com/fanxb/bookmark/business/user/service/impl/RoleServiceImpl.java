package com.fanxb.bookmark.business.user.service.impl;

import com.fanxb.bookmark.business.user.service.RoleService;
import com.fanxb.bookmark.business.user.vo.RoleAddBody;
import com.fanxb.bookmark.business.user.vo.RoleDetailRes;
import com.fanxb.bookmark.business.user.vo.RoleOptionRes;
import com.fanxb.bookmark.business.user.vo.RolePageRes;
import com.fanxb.bookmark.business.user.vo.RoleUpdateBody;
import com.fanxb.bookmark.common.dao.RoleDao;
import com.fanxb.bookmark.common.entity.po.RolePo;
import com.fanxb.bookmark.common.exception.CustomException;
import com.fanxb.bookmark.common.exception.FormDataException;
import com.fanxb.bookmark.common.permission.Permission;
import com.fanxb.bookmark.common.permission.PermissionRegistry;
import com.fanxb.bookmark.common.service.PermissionService;
import com.fanxb.bookmark.common.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 类功能简述：角色管理服务实现
 * 类功能详述：任何写操作后全量失效权限缓存；禁用角色的生效依赖 RoleDao 查询过滤 status=1
 *
 * @author fanxb
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    /**
     * 角色编码格式：小写字母、数字、下划线、冒号
     */
    private static final Pattern CODE_PATTERN = Pattern.compile("^[a-z0-9_:]+$");

    private static final int ENABLE_STATUS = 1;

    private static final int DISABLE_STATUS = 0;

    private static final int BUILT_IN = 1;

    private static final int MAX_PAGE_SIZE = 100;

    private final RoleDao roleDao;

    private final PermissionRegistry permissionRegistry;

    private final PermissionService permissionService;

    @Override
    public RolePageRes page(int pageNum, int pageSize, String keyword) {
        int safePageNum = Math.max(pageNum, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), MAX_PAGE_SIZE);
        String trimmedKeyword = StringUtil.isEmpty(keyword) ? null : keyword.trim();
        List<RolePo> list = roleDao.selectRolePage(trimmedKeyword, (safePageNum - 1) * safePageSize, safePageSize);
        return new RolePageRes(roleDao.countRole(trimmedKeyword), list);
    }

    @Override
    public RoleDetailRes detail(int roleId) {
        RolePo role = getRole(roleId);
        RoleDetailRes res = new RoleDetailRes();
        res.setRoleId(role.getRoleId());
        res.setCode(role.getCode());
        res.setName(role.getName());
        res.setDescription(role.getDescription());
        res.setStatus(role.getStatus());
        res.setBuildIn(role.getBuildIn());
        res.setCreateTime(role.getCreateTime());
        res.setUpdateTime(role.getUpdateTime());
        List<String> permissionCodes = roleDao.selectPermissionCodesByRoleId(roleId);
        res.setPermissionCodes(permissionCodes == null ? List.of() : permissionCodes);
        return res;
    }

    @Override
    public List<RoleOptionRes> options() {
        List<RolePo> roles = roleDao.selectEnabledRoles();
        List<RoleOptionRes> res = new ArrayList<>();
        if (roles == null) {
            return res;
        }
        for (RolePo role : roles) {
            RoleOptionRes option = new RoleOptionRes();
            option.setRoleId(role.getRoleId());
            option.setCode(role.getCode());
            option.setName(role.getName());
            res.add(option);
        }
        return res;
    }

    @Override
    public List<Permission> permissionTree() {
        return permissionRegistry.getPermissionTree();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(RoleAddBody body) {
        String code = body.getCode().trim();
        if (!CODE_PATTERN.matcher(code).matches()) {
            throw new FormDataException("角色编码只能由小写字母、数字、下划线、冒号组成");
        }
        if (roleDao.selectRoleIdByCode(code) != null) {
            throw new FormDataException("角色编码已存在");
        }
        String name = body.getName().trim();
        if (roleDao.countRoleByName(name, 0) > 0) {
            throw new FormDataException("角色名称已存在");
        }
        long now = System.currentTimeMillis();
        RolePo role = new RolePo();
        role.setCode(code);
        role.setName(name);
        role.setDescription(body.getDescription() == null ? "" : body.getDescription());
        role.setStatus(ENABLE_STATUS);
        role.setBuildIn(0);
        role.setCreateTime(now);
        role.setUpdateTime(now);
        roleDao.insertRole(role);
        permissionService.evictAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RoleUpdateBody body) {
        checkOperable(body.getRoleId());
        String name = body.getName().trim();
        if (roleDao.countRoleByName(name, body.getRoleId()) > 0) {
            throw new FormDataException("角色名称已存在");
        }
        roleDao.updateRole(body.getRoleId(), name, body.getDescription() == null ? "" : body.getDescription(),
                System.currentTimeMillis());
        permissionService.evictAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(int roleId, int status) {
        checkOperable(roleId);
        if (status != ENABLE_STATUS && status != DISABLE_STATUS) {
            throw new FormDataException("角色状态非法");
        }
        roleDao.updateRoleStatus(roleId, status, System.currentTimeMillis());
        //禁用后已绑用户立即失去该角色权限：RoleDao 的角色/权限查询已过滤 status=1，缓存失效即生效
        permissionService.evictAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(int roleId) {
        checkOperable(roleId);
        if (roleDao.countUserByRoleId(roleId) > 0) {
            throw new CustomException("该角色已绑定用户，无法删除");
        }
        roleDao.deleteRolePermissions(roleId);
        roleDao.deleteRole(roleId);
        permissionService.evictAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindPermissions(int roleId, List<String> permissionCodes) {
        checkOperable(roleId);
        Set<String> codes = new LinkedHashSet<>();
        if (permissionCodes != null) {
            for (String code : permissionCodes) {
                if (!permissionRegistry.permissionExists(code)) {
                    throw new FormDataException("权限code不存在：" + code);
                }
                codes.add(code);
            }
        }
        roleDao.deleteRolePermissions(roleId);
        long now = System.currentTimeMillis();
        for (String code : codes) {
            roleDao.insertRolePermission(roleId, code, now);
        }
        permissionService.evictAll();
    }

    /**
     * 查询角色并校验可操作，内置角色一律拒绝
     */
    private RolePo checkOperable(int roleId) {
        RolePo role = getRole(roleId);
        if (role.getBuildIn() != null && role.getBuildIn() == BUILT_IN) {
            throw new CustomException("内置角色不可操作");
        }
        return role;
    }

    private RolePo getRole(int roleId) {
        RolePo role = roleDao.selectRoleById(roleId);
        if (role == null) {
            throw new FormDataException("角色不存在");
        }
        return role;
    }
}
