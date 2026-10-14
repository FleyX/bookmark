package com.fanxb.bookmark.common.permission;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 类功能简述：权限注册表，权限/资源/公共 URL 的唯一定义来源
 * 类功能详述：启动时读取 classpath:permissions.yml 并做 fail-fast 校验，校验失败直接抛出异常使启动失败
 *
 * @author fanxb
 */
@Component
@Slf4j
public class PermissionRegistry {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private static final Set<String> HTTP_METHODS = Set.of("GET", "POST", "PUT", "DELETE", "PATCH");

    private static final String VIEW_PREFIX = "VIEW:";

    private static final String CONFIG_FILE = "permissions.yml";

    private final List<String> publicPatterns;

    private final Map<String, Permission> permissionMap;

    private final Set<String> allResourceCodes;

    public PermissionRegistry() {
        this(loadConfigText());
    }

    PermissionRegistry(String yamlText) {
        Map<String, Object> root = new Yaml().load(yamlText);
        if (root == null) {
            throw new IllegalStateException("权限注册表文件内容为空：" + CONFIG_FILE);
        }
        this.publicPatterns = List.copyOf(asStringList(root.get("public"), "public"));
        List<Permission> permissions = parsePermissions(root.get("permissions"));
        validate(permissions);
        Map<String, Permission> map = new LinkedHashMap<>();
        Set<String> resourceCodes = new LinkedHashSet<>();
        for (Permission permission : permissions) {
            map.put(permission.getCode(), permission);
            permission.getResources().forEach(item -> resourceCodes.add(item.getCode()));
        }
        this.permissionMap = Collections.unmodifiableMap(map);
        this.allResourceCodes = Collections.unmodifiableSet(resourceCodes);
        log.info("权限注册表加载完成，公共URL:{}，权限:{}，资源:{}", publicPatterns.size(), map.size(), resourceCodes.size());
    }

    /**
     * 免登录的 URL 匹配模式列表
     *
     * @return java.util.List<java.lang.String>
     * @author fanxb
     */
    public List<String> getPublicPatterns() {
        return publicPatterns;
    }

    /**
     * 判断请求资源是否被任一权限的资源覆盖
     *
     * @param resourceKey 请求资源，格式 METHOD:路径
     * @return boolean
     * @author fanxb
     */
    public boolean isProtected(String resourceKey) {
        return allResourceCodes.stream().anyMatch(pattern -> MATCHER.match(pattern, resourceKey));
    }

    /**
     * 将权限 code 集合展开为资源 code 集合，含祖先权限的资源
     *
     * @param permissionCodes 权限 code 集合，未知 code 静默忽略
     * @return java.util.Set<java.lang.String>
     * @author fanxb
     */
    public Set<String> expandWithAncestors(Set<String> permissionCodes) {
        Set<String> res = new LinkedHashSet<>();
        if (permissionCodes == null) {
            return res;
        }
        for (String code : permissionCodes) {
            Permission permission = permissionMap.get(code);
            while (permission != null) {
                permission.getResources().forEach(item -> res.add(item.getCode()));
                permission = permission.getParent() == null ? null : permissionMap.get(permission.getParent());
            }
        }
        return res;
    }

    /**
     * 全部资源 code，供超级管理员使用
     *
     * @return java.util.Set<java.lang.String>
     * @author fanxb
     */
    public Set<String> getAllResourceCodes() {
        return allResourceCodes;
    }

    /**
     * 权限 code 是否存在
     *
     * @param code 权限 code
     * @return boolean
     * @author fanxb
     */
    public boolean permissionExists(String code) {
        return permissionMap.containsKey(code);
    }

    /**
     * 权限树，含全部权限及其资源，保持注册表声明顺序，供前端渲染勾选树
     *
     * @return java.util.List<com.fanxb.bookmark.common.permission.Permission>
     * @author fanxb
     */
    public List<Permission> getPermissionTree() {
        return List.copyOf(permissionMap.values());
    }

    private static String loadConfigText() {
        try (InputStream inputStream = new ClassPathResource(CONFIG_FILE).getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("读取权限注册表文件失败：" + CONFIG_FILE, e);
        }
    }

    private static List<String> asStringList(Object node, String section) {
        if (node == null) {
            return List.of();
        }
        if (!(node instanceof List<?> list)) {
            throw new IllegalStateException("权限注册表 " + section + " 段格式非法，应为列表");
        }
        List<String> res = new ArrayList<>(list.size());
        for (Object item : list) {
            if (item == null || !StringUtils.hasText(item.toString())) {
                throw new IllegalStateException("权限注册表 " + section + " 段存在空值");
            }
            res.add(item.toString());
        }
        return res;
    }

    private static List<Permission> parsePermissions(Object node) {
        if (node == null) {
            return List.of();
        }
        if (!(node instanceof List<?> list)) {
            throw new IllegalStateException("权限注册表 permissions 段格式非法，应为列表");
        }
        List<Permission> res = new ArrayList<>(list.size());
        for (Object item : list) {
            if (!(item instanceof Map<?, ?> map)) {
                throw new IllegalStateException("权限注册表 permissions 段元素格式非法，应为对象");
            }
            String code = requireText(map.get("code"), "权限 code");
            String name = requireText(map.get("name"), "权限名称");
            String parent = map.get("parent") == null ? null : requireText(map.get("parent"), "父权限 code");
            res.add(new Permission(code, name, parent, parseResources(map.get("resources"))));
        }
        return res;
    }

    private static List<PermissionResource> parseResources(Object node) {
        if (node == null) {
            return List.of();
        }
        if (!(node instanceof List<?> list)) {
            throw new IllegalStateException("权限注册表 resources 段格式非法，应为列表");
        }
        List<PermissionResource> res = new ArrayList<>(list.size());
        for (Object item : list) {
            if (!(item instanceof Map<?, ?> map)) {
                throw new IllegalStateException("权限注册表 resources 段元素格式非法，应为对象");
            }
            res.add(new PermissionResource(requireText(map.get("code"), "资源 code"), requireText(map.get("name"), "资源名称")));
        }
        return res;
    }

    private static String requireText(Object value, String field) {
        if (value == null || !StringUtils.hasText(value.toString())) {
            throw new IllegalStateException("权限注册表 " + field + " 不能为空");
        }
        return value.toString();
    }

    private static void validate(List<Permission> permissions) {
        Map<String, Permission> byCode = new LinkedHashMap<>();
        for (Permission permission : permissions) {
            if (byCode.putIfAbsent(permission.getCode(), permission) != null) {
                throw new IllegalStateException("权限注册表权限 code 重复：" + permission.getCode());
            }
        }
        Set<String> resourceCodes = new HashSet<>();
        for (Permission permission : permissions) {
            for (PermissionResource resource : permission.getResources()) {
                checkCodeFormat(resource.getCode(), "资源");
                if (!resourceCodes.add(resource.getCode())) {
                    throw new IllegalStateException("权限注册表资源 code 重复：" + resource.getCode());
                }
            }
        }
        for (Permission permission : permissions) {
            if (permission.getParent() != null && !byCode.containsKey(permission.getParent())) {
                throw new IllegalStateException("权限注册表父权限不存在：" + permission.getCode() + " -> " + permission.getParent());
            }
        }
        for (Permission permission : permissions) {
            Set<String> visited = new HashSet<>();
            Permission current = permission;
            while (current != null) {
                if (!visited.add(current.getCode())) {
                    throw new IllegalStateException("权限注册表权限树存在环：" + permission.getCode());
                }
                current = current.getParent() == null ? null : byCode.get(current.getParent());
            }
        }
    }

    private static void checkCodeFormat(String code, String type) {
        if (code.startsWith(VIEW_PREFIX)) {
            if (code.length() == VIEW_PREFIX.length()) {
                throw new IllegalStateException("权限注册表" + type + " code 格式非法：" + code);
            }
            return;
        }
        int index = code.indexOf(':');
        if (index <= 0) {
            throw new IllegalStateException("权限注册表" + type + " code 格式非法：" + code);
        }
        String method = code.substring(0, index);
        String path = code.substring(index + 1);
        if (!HTTP_METHODS.contains(method) || !path.startsWith("/")) {
            throw new IllegalStateException("权限注册表" + type + " code 格式非法：" + code);
        }
    }
}
