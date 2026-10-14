package com.fanxb.bookmark.common.permission;

import lombok.Data;

/**
 * 类功能简述：权限注册表中的资源，最小鉴权单元
 * 类功能详述：code 格式为 METHOD:路径（后端接口）或 VIEW:路径（前端页面）
 *
 * @author fanxb
 */
@Data
public class PermissionResource {

    private final String code;

    private final String name;
}
