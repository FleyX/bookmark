package com.fanxb.bookmark.common.permission;

import lombok.Data;

import java.util.List;

/**
 * 类功能简述：权限注册表中的权限，资源的打包分组
 *
 * @author fanxb
 */
@Data
public class Permission {

    private final String code;

    private final String name;

    /**
     * 父权限 code，无父权限时为 null
     */
    private final String parent;

    private final List<PermissionResource> resources;
}
