package com.fanxb.bookmark.business.user.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 类功能简述：角色启用/禁用表单
 *
 * @author fanxb
 */
@Data
public class RoleStatusBody {

    @NotNull(message = "角色id不能为空")
    private Integer roleId;

    @NotNull(message = "角色状态不能为空")
    private Integer status;
}
