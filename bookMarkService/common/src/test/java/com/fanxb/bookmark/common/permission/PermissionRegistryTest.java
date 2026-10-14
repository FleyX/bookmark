package com.fanxb.bookmark.common.permission;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 类功能简述：权限注册表单测
 *
 * @author fanxb
 */
class PermissionRegistryTest {

    private static final String NORMAL_CONFIG = """
            public:
              - "PUT:/user"
              - "GET:/home/pin"
            permissions:
              - code: system:user
                name: 用户管理
                resources:
                  - { code: "VIEW:/admin/user", name: "用户管理页面" }
                  - { code: "GET:/user/manage/**", name: "用户分页列表" }
              - code: system:user:edit
                name: 用户编辑
                parent: system:user
                resources:
                  - { code: "POST:/user/manage/edit", name: "编辑用户" }
            """;

    @Test
    void testParseNormal() {
        PermissionRegistry registry = new PermissionRegistry(NORMAL_CONFIG);
        assertEquals(2, registry.getPublicPatterns().size());
        assertTrue(registry.getPublicPatterns().contains("PUT:/user"));
        assertTrue(registry.permissionExists("system:user"));
        assertFalse(registry.permissionExists("not:exist"));
        assertEquals(3, registry.getAllResourceCodes().size());
        assertTrue(registry.isProtected("GET:/user/manage/page"));
        assertFalse(registry.isProtected("GET:/user/currentUserInfo"));
    }

    @Test
    void testGetPermissionTree() {
        PermissionRegistry registry = new PermissionRegistry(NORMAL_CONFIG);
        List<Permission> tree = registry.getPermissionTree();
        assertEquals(2, tree.size());
        //保持注册表声明顺序，且含资源
        assertEquals("system:user", tree.get(0).getCode());
        assertEquals("system:user:edit", tree.get(1).getCode());
        assertEquals(Set.of("VIEW:/admin/user", "GET:/user/manage/**"),
                tree.get(0).getResources().stream().map(PermissionResource::getCode).collect(Collectors.toSet()));
    }

    @Test
    void testExpandWithAncestors() {
        PermissionRegistry registry = new PermissionRegistry(NORMAL_CONFIG);
        assertEquals(Set.of("POST:/user/manage/edit", "VIEW:/admin/user", "GET:/user/manage/**"),
                registry.expandWithAncestors(Set.of("system:user:edit")));
        assertEquals(Set.of("VIEW:/admin/user", "GET:/user/manage/**"),
                registry.expandWithAncestors(Set.of("system:user")));
        assertTrue(registry.expandWithAncestors(Set.of("not:exist")).isEmpty());
    }

    @Test
    void testDuplicateResourceCode() {
        assertThrows(IllegalStateException.class, () -> new PermissionRegistry("""
                permissions:
                  - code: a
                    name: A
                    resources:
                      - { code: "GET:/a", name: "a" }
                  - code: b
                    name: B
                    resources:
                      - { code: "GET:/a", name: "a" }
                """));
    }

    @Test
    void testInvalidResourceCodeFormat() {
        assertThrows(IllegalStateException.class, () -> new PermissionRegistry("""
                permissions:
                  - code: a
                    name: A
                    resources:
                      - { code: "FOO:/a", name: "a" }
                """));
    }

    @Test
    void testParentNotExist() {
        assertThrows(IllegalStateException.class, () -> new PermissionRegistry("""
                permissions:
                  - code: a
                    name: A
                    parent: notExist
                    resources:
                      - { code: "GET:/a", name: "a" }
                """));
    }

    @Test
    void testCycle() {
        assertThrows(IllegalStateException.class, () -> new PermissionRegistry("""
                permissions:
                  - code: a
                    name: A
                    parent: b
                    resources:
                      - { code: "GET:/a", name: "a" }
                  - code: b
                    name: B
                    parent: a
                    resources:
                      - { code: "GET:/b", name: "b" }
                """));
    }
}
