package com.fanxb.bookmark.common.service.impl;

import com.fanxb.bookmark.common.constant.CommonConstant;
import com.fanxb.bookmark.common.dao.RoleDao;
import com.fanxb.bookmark.common.permission.PermissionRegistry;
import com.fanxb.bookmark.common.service.PermissionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类功能简述：用户权限解析服务，用户到资源的解析结果进程内缓存
 *
 * @author fanxb
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final RoleDao roleDao;

    private final PermissionRegistry permissionRegistry;

    private final Map<Integer, UserPermission> cache = new ConcurrentHashMap<>();

    @Override
    public boolean isSuperAdmin(int userId) {
        return getUserPermission(userId).isSuperAdmin();
    }

    @Override
    public Set<String> resolveResourceCodes(int userId) {
        return getUserPermission(userId).getResourceCodes();
    }

    @Override
    public void evictUser(int userId) {
        cache.remove(userId);
    }

    @Override
    public void evictAll() {
        cache.clear();
    }

    private UserPermission getUserPermission(int userId) {
        return cache.computeIfAbsent(userId, this::loadUserPermission);
    }

    private UserPermission loadUserPermission(int userId) {
        List<String> roleCodes = roleDao.selectRoleCodesByUserId(userId);
        boolean superAdmin = roleCodes != null && roleCodes.contains(CommonConstant.SUPER_ADMIN_ROLE_CODE);
        Set<String> resourceCodes;
        if (superAdmin) {
            resourceCodes = permissionRegistry.getAllResourceCodes();
        } else {
            List<String> permissionCodes = roleDao.selectPermissionCodesByUserId(userId);
            resourceCodes = permissionRegistry.expandWithAncestors(permissionCodes == null ? Set.of() : new HashSet<>(permissionCodes));
        }
        return new UserPermission(superAdmin, Set.copyOf(resourceCodes));
    }

    /**
     * 用户权限缓存内容
     */
    @Data
    private static class UserPermission {

        private final boolean superAdmin;

        private final Set<String> resourceCodes;
    }
}
