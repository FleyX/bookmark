package com.fanxb.bookmark.common.permission;

import com.fanxb.bookmark.common.dao.RoleDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 类功能简述：悬空权限绑定审计单测
 *
 * @author fanxb
 */
@ExtendWith(MockitoExtension.class)
class PermissionBindingAuditorTest {

    @Mock
    private RoleDao roleDao;

    @Mock
    private PermissionRegistry permissionRegistry;

    @Test
    void testRunScanAllBoundCodes() {
        when(roleDao.selectDistinctPermissionCodes()).thenReturn(List.of("system:user", "ghost:code"));
        when(permissionRegistry.permissionExists("system:user")).thenReturn(true);
        when(permissionRegistry.permissionExists("ghost:code")).thenReturn(false);

        new PermissionBindingAuditor(roleDao, permissionRegistry).run(new DefaultApplicationArguments());

        verify(roleDao).selectDistinctPermissionCodes();
        verify(permissionRegistry).permissionExists("system:user");
        verify(permissionRegistry).permissionExists("ghost:code");
    }

    @Test
    void testRunWithEmptyBindings() {
        when(roleDao.selectDistinctPermissionCodes()).thenReturn(List.of());

        new PermissionBindingAuditor(roleDao, permissionRegistry).run(new DefaultApplicationArguments());
    }
}
