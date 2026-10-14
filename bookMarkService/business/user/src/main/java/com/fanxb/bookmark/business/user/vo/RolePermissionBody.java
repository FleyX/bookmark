package com.fanxb.bookmark.business.user.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 类功能简述：角色绑定权限表单，全量替换
 *
 * @author fanxb
 */
@Data
public class RolePermissionBody {

    @NotNull(message = "角色id不能为空")
    private Integer roleId;

    /**
     * 权限code列表，空列表表示清空绑定
     */
    private List<String> permissionCodes;
}
