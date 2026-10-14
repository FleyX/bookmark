package com.fanxb.bookmark.common.service.impl;

import com.fanxb.bookmark.common.dao.RoleDao;
import com.fanxb.bookmark.common.permission.PermissionRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 类功能简述：用户权限解析服务单测
 *
 * @author fanxb
 */
@ExtendWith(MockitoExtension.class)
class PermissionServiceImplTest {

    @Mock
    private RoleDao roleDao;

    @Mock
    private PermissionRegistry permissionRegistry;

    private PermissionServiceImpl permissionService;

    @BeforeEach
    void setUp() {
        permissionService = new PermissionServiceImpl(roleDao, permissionRegistry);
    }

    @Test
    void testResolveResourceCodesWithCache() {
        when(roleDao.selectRoleCodesByUserId(1)).thenReturn(List.of("role_a"));
        when(roleDao.selectPermissionCodesByUserId(1)).thenReturn(List.of("system:user"));
        when(permissionRegistry.expandWithAncestors(anySet())).thenReturn(Set.of("GET:/user/manage/page"));

        assertEquals(Set.of("GET:/user/manage/page"), permissionService.resolveResourceCodes(1));
        //第二次走缓存，不再查库
        assertEquals(Set.of("GET:/user/manage/page"), permissionService.resolveResourceCodes(1));
        verify(roleDao, times(1)).selectRoleCodesByUserId(1);
        verify(roleDao, times(1)).selectPermissionCodesByUserId(1);
    }

    @Test
    void testEvictUser() {
        when(roleDao.selectRoleCodesByUserId(1)).thenReturn(List.of("role_a"));
        when(roleDao.selectPermissionCodesByUserId(1)).thenReturn(List.of("system:user"));
        when(permissionRegistry.expandWithAncestors(anySet())).thenReturn(Set.of("GET:/user/manage/page"));

        permissionService.resolveResourceCodes(1);
        permissionService.evictUser(1);
        permissionService.resolveResourceCodes(1);
        verify(roleDao, times(2)).selectRoleCodesByUserId(1);
    }

    @Test
    void testEvictAll() {
        when(roleDao.selectRoleCodesByUserId(anyInt())).thenReturn(List.of("role_a"));
        when(roleDao.selectPermissionCodesByUserId(anyInt())).thenReturn(List.of());
        when(permissionRegistry.expandWithAncestors(anySet())).thenReturn(Set.of());

        permissionService.resolveResourceCodes(1);
        permissionService.resolveResourceCodes(2);
        permissionService.evictAll();
        permissionService.resolveResourceCodes(1);
        verify(roleDao, times(2)).selectRoleCodesByUserId(1);
    }

    @Test
    void testSuperAdminShortCircuit() {
        when(roleDao.selectRoleCodesByUserId(1)).thenReturn(List.of("role_a", "super_admin"));
        when(permissionRegistry.getAllResourceCodes()).thenReturn(Set.of("VIEW:/admin/user", "GET:/user/manage/page"));

        assertTrue(permissionService.isSuperAdmin(1));
        assertEquals(Set.of("VIEW:/admin/user", "GET:/user/manage/page"), permissionService.resolveResourceCodes(1));
        verify(roleDao, never()).selectPermissionCodesByUserId(anyInt());
    }

    @Test
    void testNotSuperAdmin() {
        when(roleDao.selectRoleCodesByUserId(2)).thenReturn(List.of("role_a"));
        when(roleDao.selectPermissionCodesByUserId(2)).thenReturn(List.of());
        when(permissionRegistry.expandWithAncestors(anySet())).thenReturn(Set.of());

        assertFalse(permissionService.isSuperAdmin(2));
        assertTrue(permissionService.resolveResourceCodes(2).isEmpty());
    }
}
