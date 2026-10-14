package com.fanxb.bookmark.business.user.service.impl;

import com.fanxb.bookmark.business.user.vo.RoleAddBody;
import com.fanxb.bookmark.business.user.vo.RoleDetailRes;
import com.fanxb.bookmark.business.user.vo.RoleOptionRes;
import com.fanxb.bookmark.business.user.vo.RolePageRes;
import com.fanxb.bookmark.business.user.vo.RoleUpdateBody;
import com.fanxb.bookmark.common.dao.RoleDao;
import com.fanxb.bookmark.common.entity.po.RolePo;
import com.fanxb.bookmark.common.exception.CustomException;
import com.fanxb.bookmark.common.exception.FormDataException;
import com.fanxb.bookmark.common.permission.PermissionRegistry;
import com.fanxb.bookmark.common.service.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 类功能简述：角色管理服务单测
 *
 * @author fanxb
 */
@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleDao roleDao;

    @Mock
    private PermissionRegistry permissionRegistry;

    @Mock
    private PermissionService permissionService;

    private RoleServiceImpl roleService;

    @BeforeEach
    void setUp() {
        roleService = new RoleServiceImpl(roleDao, permissionRegistry, permissionService);
    }

    @Test
    void testAddDuplicateCode() {
        RoleAddBody body = addBody("dup", "重复角色");
        when(roleDao.selectRoleIdByCode("dup")).thenReturn(5);

        FormDataException e = assertThrows(FormDataException.class, () -> roleService.add(body));
        assertEquals("角色编码已存在", e.getMessage());
        verify(roleDao, never()).insertRole(any(RolePo.class));
        verify(permissionService, never()).evictAll();
    }

    @Test
    void testAddInvalidCodeFormat() {
        RoleAddBody body = addBody("Dup Role", "非法编码");

        assertThrows(FormDataException.class, () -> roleService.add(body));
        verify(roleDao, never()).insertRole(any(RolePo.class));
    }

    @Test
    void testAddDuplicateName() {
        RoleAddBody body = addBody("ops_role", "运维角色");
        when(roleDao.selectRoleIdByCode("ops_role")).thenReturn(null);
        when(roleDao.countRoleByName("运维角色", 0)).thenReturn(1);

        FormDataException e = assertThrows(FormDataException.class, () -> roleService.add(body));
        assertEquals("角色名称已存在", e.getMessage());
        verify(roleDao, never()).insertRole(any(RolePo.class));
    }

    @Test
    void testAddSuccess() {
        RoleAddBody body = addBody("ops_role", "运维角色");
        when(roleDao.selectRoleIdByCode("ops_role")).thenReturn(null);
        when(roleDao.countRoleByName("运维角色", 0)).thenReturn(0);

        roleService.add(body);

        ArgumentCaptor<RolePo> captor = ArgumentCaptor.forClass(RolePo.class);
        verify(roleDao).insertRole(captor.capture());
        RolePo inserted = captor.getValue();
        assertEquals("ops_role", inserted.getCode());
        assertEquals("运维角色", inserted.getName());
        assertEquals(1, inserted.getStatus().intValue());
        assertEquals(0, inserted.getBuildIn().intValue());
        verify(permissionService).evictAll();
    }

    @Test
    void testUpdateSuperAdmin() {
        when(roleDao.selectRoleById(1)).thenReturn(buildRole(1, 1));
        RoleUpdateBody body = new RoleUpdateBody();
        body.setRoleId(1);
        body.setName("改名");

        CustomException e = assertThrows(CustomException.class, () -> roleService.update(body));
        assertEquals("内置角色不可操作", e.getMessage());
        verify(roleDao, never()).updateRole(anyInt(), anyString(), anyString(), anyLong());
        verify(permissionService, never()).evictAll();
    }

    @Test
    void testUpdateDuplicateName() {
        when(roleDao.selectRoleById(2)).thenReturn(buildRole(2, 0));
        when(roleDao.countRoleByName("已存在", 2)).thenReturn(1);
        RoleUpdateBody body = new RoleUpdateBody();
        body.setRoleId(2);
        body.setName("已存在");

        assertThrows(FormDataException.class, () -> roleService.update(body));
        verify(roleDao, never()).updateRole(anyInt(), anyString(), anyString(), anyLong());
    }

    @Test
    void testUpdateSuccess() {
        when(roleDao.selectRoleById(2)).thenReturn(buildRole(2, 0));
        when(roleDao.countRoleByName("新名字", 2)).thenReturn(0);
        RoleUpdateBody body = new RoleUpdateBody();
        body.setRoleId(2);
        body.setName("新名字");
        body.setDescription("新描述");

        roleService.update(body);

        verify(roleDao).updateRole(eq(2), eq("新名字"), eq("新描述"), anyLong());
        verify(permissionService).evictAll();
    }

    @Test
    void testUpdateStatusSuperAdmin() {
        when(roleDao.selectRoleById(1)).thenReturn(buildRole(1, 1));

        CustomException e = assertThrows(CustomException.class, () -> roleService.updateStatus(1, 0));
        assertEquals("内置角色不可操作", e.getMessage());
        verify(roleDao, never()).updateRoleStatus(anyInt(), anyInt(), anyLong());
    }

    @Test
    void testUpdateStatusInvalidStatus() {
        when(roleDao.selectRoleById(2)).thenReturn(buildRole(2, 0));

        assertThrows(FormDataException.class, () -> roleService.updateStatus(2, 5));
        verify(roleDao, never()).updateRoleStatus(anyInt(), anyInt(), anyLong());
    }

    /**
     * 禁用角色的"立即生效"契约：RoleDao 的 selectRoleCodesByUserId / selectPermissionCodesByUserId
     * 均只返回 status=1 的角色，故此处只需保证禁用后 evictAll 被调用，缓存重算即生效
     */
    @Test
    void testDisableRoleEvictAll() {
        when(roleDao.selectRoleById(2)).thenReturn(buildRole(2, 0));

        roleService.updateStatus(2, 0);

        verify(roleDao).updateRoleStatus(eq(2), eq(0), anyLong());
        verify(permissionService).evictAll();
    }

    @Test
    void testDeleteSuperAdmin() {
        when(roleDao.selectRoleById(1)).thenReturn(buildRole(1, 1));

        CustomException e = assertThrows(CustomException.class, () -> roleService.delete(1));
        assertEquals("内置角色不可操作", e.getMessage());
        verify(roleDao, never()).deleteRole(anyInt());
        verify(permissionService, never()).evictAll();
    }

    @Test
    void testDeleteRoleWithUserBinding() {
        when(roleDao.selectRoleById(2)).thenReturn(buildRole(2, 0));
        when(roleDao.countUserByRoleId(2)).thenReturn(1);

        CustomException e = assertThrows(CustomException.class, () -> roleService.delete(2));
        assertEquals("该角色已绑定用户，无法删除", e.getMessage());
        verify(roleDao, never()).deleteRole(anyInt());
        verify(permissionService, never()).evictAll();
    }

    @Test
    void testDeleteSuccess() {
        when(roleDao.selectRoleById(2)).thenReturn(buildRole(2, 0));
        when(roleDao.countUserByRoleId(2)).thenReturn(0);

        roleService.delete(2);

        verify(roleDao).deleteRolePermissions(2);
        verify(roleDao).deleteRole(2);
        verify(permissionService).evictAll();
    }

    @Test
    void testDeleteNotExistRole() {
        when(roleDao.selectRoleById(9)).thenReturn(null);

        assertThrows(FormDataException.class, () -> roleService.delete(9));
        verify(roleDao, never()).deleteRole(anyInt());
    }

    @Test
    void testBindSuperAdmin() {
        when(roleDao.selectRoleById(1)).thenReturn(buildRole(1, 1));

        CustomException e = assertThrows(CustomException.class,
                () -> roleService.bindPermissions(1, List.of("system:role")));
        assertEquals("内置角色不可操作", e.getMessage());
        verify(roleDao, never()).deleteRolePermissions(anyInt());
        verify(permissionService, never()).evictAll();
    }

    @Test
    void testBindUnknownPermissionCode() {
        when(roleDao.selectRoleById(2)).thenReturn(buildRole(2, 0));

        FormDataException e = assertThrows(FormDataException.class,
                () -> roleService.bindPermissions(2, List.of("not:exist")));
        assertEquals("权限code不存在：not:exist", e.getMessage());
        verify(roleDao, never()).deleteRolePermissions(anyInt());
        verify(permissionService, never()).evictAll();
    }

    @Test
    void testBindPermissionsReplaceAll() {
        when(roleDao.selectRoleById(2)).thenReturn(buildRole(2, 0));
        when(permissionRegistry.permissionExists("system:user")).thenReturn(true);
        when(permissionRegistry.permissionExists("system:role")).thenReturn(true);

        //重复code只落一条
        roleService.bindPermissions(2, List.of("system:user", "system:role", "system:user"));

        verify(roleDao).deleteRolePermissions(2);
        verify(roleDao).insertRolePermission(eq(2), eq("system:user"), anyLong());
        verify(roleDao).insertRolePermission(eq(2), eq("system:role"), anyLong());
        verify(roleDao, times(2)).insertRolePermission(anyInt(), anyString(), anyLong());
        verify(permissionService).evictAll();
    }

    @Test
    void testBindEmptyPermissions() {
        when(roleDao.selectRoleById(2)).thenReturn(buildRole(2, 0));

        roleService.bindPermissions(2, List.of());

        verify(roleDao).deleteRolePermissions(2);
        verify(roleDao, never()).insertRolePermission(anyInt(), anyString(), anyLong());
        verify(permissionService).evictAll();
    }

    @Test
    void testDetail() {
        when(roleDao.selectRoleById(2)).thenReturn(buildRole(2, 0));
        when(roleDao.selectPermissionCodesByRoleId(2)).thenReturn(List.of("system:user"));

        RoleDetailRes res = roleService.detail(2);

        assertEquals(Integer.valueOf(2), res.getRoleId());
        assertEquals("role_2", res.getCode());
        assertEquals(List.of("system:user"), res.getPermissionCodes());
    }

    @Test
    void testOptions() {
        when(roleDao.selectEnabledRoles()).thenReturn(List.of(buildRole(2, 0)));

        List<RoleOptionRes> res = roleService.options();

        assertEquals(1, res.size());
        assertEquals(Integer.valueOf(2), res.get(0).getRoleId());
        assertEquals("role_2", res.get(0).getCode());
    }

    @Test
    void testPageClampAndTotal() {
        when(roleDao.selectRolePage("abc", 0, 100)).thenReturn(List.of(buildRole(2, 0)));
        when(roleDao.countRole("abc")).thenReturn(1L);

        //pageNum<1 归 1，pageSize 上限 100
        RolePageRes res = roleService.page(0, 1000, "abc");

        assertEquals(1L, res.getTotal());
        assertEquals(1, res.getList().size());
    }

    private RoleAddBody addBody(String code, String name) {
        RoleAddBody body = new RoleAddBody();
        body.setCode(code);
        body.setName(name);
        body.setDescription("描述");
        return body;
    }

    private RolePo buildRole(int roleId, int buildIn) {
        RolePo role = new RolePo();
        role.setRoleId(roleId);
        role.setCode("role_" + roleId);
        role.setName("角色" + roleId);
        role.setDescription("");
        role.setStatus(1);
        role.setBuildIn(buildIn);
        role.setCreateTime(1L);
        role.setUpdateTime(1L);
        return role;
    }
}
