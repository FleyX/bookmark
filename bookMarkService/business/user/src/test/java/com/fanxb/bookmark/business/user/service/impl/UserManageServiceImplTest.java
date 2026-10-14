package com.fanxb.bookmark.business.user.service.impl;

import com.fanxb.bookmark.business.api.BookmarkApi;
import com.fanxb.bookmark.business.user.dao.UserDao;
import com.fanxb.bookmark.business.user.vo.UserDetailRes;
import com.fanxb.bookmark.business.user.vo.UserPageRes;
import com.fanxb.bookmark.common.constant.CommonConstant;
import com.fanxb.bookmark.common.dao.RoleDao;
import com.fanxb.bookmark.common.entity.po.RolePo;
import com.fanxb.bookmark.common.entity.po.User;
import com.fanxb.bookmark.common.exception.CustomException;
import com.fanxb.bookmark.common.exception.FormDataException;
import com.fanxb.bookmark.common.service.PermissionService;
import com.fanxb.bookmark.common.service.UserStateService;
import com.fanxb.bookmark.common.util.HashUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 类功能简述：用户管理服务单测
 *
 * @author fanxb
 */
@ExtendWith(MockitoExtension.class)
class UserManageServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private RoleDao roleDao;

    @Mock
    private UserStateService userStateService;

    @Mock
    private PermissionService permissionService;

    @Mock
    private BookmarkApi bookmarkApi;

    private UserManageServiceImpl userManageService;

    @BeforeEach
    void setUp() {
        userManageService = new UserManageServiceImpl(userDao, roleDao, userStateService, permissionService, bookmarkApi);
    }

    @Test
    void testUpdateStatusRejectSuperAdmin() {
        when(roleDao.selectRoleCodesByUserId(2)).thenReturn(List.of("super_admin"));

        CustomException e = assertThrows(CustomException.class, () -> userManageService.updateStatus(2, 0));
        assertEquals("不能操作超级管理员", e.getMessage());
        verify(userDao, never()).updateStatus(anyInt(), anyInt());
        verify(userStateService, never()).disable(anyInt());
    }

    @Test
    void testUpdateStatusInvalidStatus() {
        assertThrows(FormDataException.class, () -> userManageService.updateStatus(2, 5));
        verify(userDao, never()).updateStatus(anyInt(), anyInt());
    }

    @Test
    void testDisableUser() {
        mockOperableUser(2);
        when(userDao.selectByUserIdOrGithubId(2, null)).thenReturn(buildUser(2, 1));

        userManageService.updateStatus(2, 0);

        verify(userDao).updateStatus(2, 0);
        verify(userStateService).disable(2);
        verify(userStateService, never()).enable(anyInt());
    }

    @Test
    void testEnableUser() {
        mockOperableUser(2);
        when(userDao.selectByUserIdOrGithubId(2, null)).thenReturn(buildUser(2, 0));

        userManageService.updateStatus(2, 1);

        verify(userDao).updateStatus(2, 1);
        verify(userStateService).enable(2);
        verify(userStateService, never()).disable(anyInt());
    }

    @Test
    void testUpdateStatusUserNotExist() {
        mockOperableUser(9);

        FormDataException e = assertThrows(FormDataException.class, () -> userManageService.updateStatus(9, 0));
        assertEquals("用户不存在", e.getMessage());
        verify(userDao, never()).updateStatus(anyInt(), anyInt());
        verify(userStateService, never()).disable(anyInt());
    }

    @Test
    void testAssignRolesRejectSuperAdmin() {
        when(roleDao.selectRoleCodesByUserId(2)).thenReturn(List.of("super_admin"));

        CustomException e = assertThrows(CustomException.class, () -> userManageService.assignRoles(2, List.of(3)));
        assertEquals("不能操作超级管理员", e.getMessage());
        verify(roleDao, never()).deleteUserRoles(anyInt());
        verify(permissionService, never()).evictUser(anyInt());
    }

    @Test
    void testAssignRolesDisabledRoleRejected() {
        mockOperableUser(2);
        when(userDao.selectByUserIdOrGithubId(2, null)).thenReturn(buildUser(2, 1));
        when(roleDao.selectRoleById(3)).thenReturn(buildRole(3, 0));

        FormDataException e = assertThrows(FormDataException.class, () -> userManageService.assignRoles(2, List.of(3)));
        assertEquals("角色不存在或已禁用：3", e.getMessage());
        verify(roleDao, never()).deleteUserRoles(anyInt());
        verify(permissionService, never()).evictUser(anyInt());
    }

    @Test
    void testAssignRolesGrantSuperAdminRoleRejected() {
        mockOperableUser(2);
        when(userDao.selectByUserIdOrGithubId(2, null)).thenReturn(buildUser(2, 1));
        RolePo superAdminRole = buildRole(1, 1);
        superAdminRole.setCode(CommonConstant.SUPER_ADMIN_ROLE_CODE);
        when(roleDao.selectRoleById(1)).thenReturn(superAdminRole);

        CustomException e = assertThrows(CustomException.class, () -> userManageService.assignRoles(2, List.of(1)));
        assertEquals("超级管理员角色不允许分配", e.getMessage());
        verify(roleDao, never()).deleteUserRoles(anyInt());
        verify(permissionService, never()).evictUser(anyInt());
    }

    @Test
    void testAssignRolesReplaceAll() {
        mockOperableUser(2);
        when(userDao.selectByUserIdOrGithubId(2, null)).thenReturn(buildUser(2, 1));
        when(roleDao.selectRoleById(3)).thenReturn(buildRole(3, 1));
        when(roleDao.selectRoleById(4)).thenReturn(buildRole(4, 1));

        //重复角色只落一条
        userManageService.assignRoles(2, List.of(3, 4, 3));

        verify(roleDao).deleteUserRoles(2);
        verify(roleDao, times(2)).insertUserRole(eq(2), anyInt(), anyLong());
        verify(roleDao).insertUserRole(eq(2), eq(3), anyLong());
        verify(roleDao).insertUserRole(eq(2), eq(4), anyLong());
        verify(permissionService).evictUser(2);
    }

    @Test
    void testAssignRolesEmptyClearAll() {
        mockOperableUser(2);
        when(userDao.selectByUserIdOrGithubId(2, null)).thenReturn(buildUser(2, 1));

        userManageService.assignRoles(2, List.of());

        verify(roleDao).deleteUserRoles(2);
        verify(roleDao, never()).insertUserRole(anyInt(), anyInt(), anyLong());
        verify(permissionService).evictUser(2);
    }

    @Test
    void testResetPasswordRejectSuperAdmin() {
        when(roleDao.selectRoleCodesByUserId(2)).thenReturn(List.of("super_admin"));

        CustomException e = assertThrows(CustomException.class, () -> userManageService.resetPassword(2, "newPass"));
        assertEquals("不能操作超级管理员", e.getMessage());
        verify(userDao, never()).updatePasswordByUserId(anyInt(), anyString());
        verify(userStateService, never()).markPasswordReset(anyInt());
    }

    @Test
    void testResetPasswordSuccess() {
        mockOperableUser(2);
        when(userDao.selectByUserIdOrGithubId(2, null)).thenReturn(buildUser(2, 1));

        userManageService.resetPassword(2, "newPass");

        verify(userDao).updatePasswordByUserId(2, HashUtil.getPassword("newPass"));
        verify(userStateService).markPasswordReset(2);
    }

    @Test
    void testDetail() {
        when(userDao.selectByUserIdOrGithubId(2, null)).thenReturn(buildUser(2, 1));
        when(bookmarkApi.countBookmarkByUserId(2)).thenReturn(12L);
        when(roleDao.selectRolesByUserId(2)).thenReturn(List.of(buildRole(3, 1)));

        UserDetailRes res = userManageService.detail(2);

        assertEquals(Integer.valueOf(2), res.getUserId());
        assertEquals("user2", res.getUsername());
        assertEquals(1, res.getStatus().intValue());
        assertEquals(12L, res.getBookmarkCount());
        assertEquals(1, res.getRoles().size());
        assertEquals(Integer.valueOf(3), res.getRoles().get(0).getRoleId());
    }

    @Test
    void testDetailUserNotExist() {
        when(userDao.selectByUserIdOrGithubId(9, null)).thenReturn(null);

        FormDataException e = assertThrows(FormDataException.class, () -> userManageService.detail(9));
        assertEquals("用户不存在", e.getMessage());
    }

    @Test
    void testPageClampAndTotal() {
        when(userDao.selectUserPage("abc", 0, 100)).thenReturn(List.of(buildUser(2, 1)));
        when(userDao.countUser("abc")).thenReturn(1L);

        //pageNum<1 归 1，pageSize 上限 100
        UserPageRes res = userManageService.page(0, 1000, "abc");

        assertEquals(1L, res.getTotal());
        assertEquals(1, res.getList().size());
        assertEquals("user2", res.getList().get(0).getUsername());
    }

    @Test
    void testPageBlankKeyword() {
        when(userDao.selectUserPage(null, 0, 10)).thenReturn(List.of());
        when(userDao.countUser(null)).thenReturn(0L);

        UserPageRes res = userManageService.page(1, 10, "  ");

        assertEquals(0L, res.getTotal());
        assertEquals(0, res.getList().size());
    }

    private void mockOperableUser(int userId) {
        when(roleDao.selectRoleCodesByUserId(userId)).thenReturn(List.of());
    }

    private User buildUser(int userId, int status) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername("user" + userId);
        user.setEmail("user" + userId + "@test.com");
        user.setIcon("");
        user.setCreateTime(1L);
        user.setLastLoginTime(2L);
        user.setStatus(status);
        return user;
    }

    private RolePo buildRole(int roleId, int status) {
        RolePo role = new RolePo();
        role.setRoleId(roleId);
        role.setCode("role_" + roleId);
        role.setName("角色" + roleId);
        role.setStatus(status);
        return role;
    }
}
