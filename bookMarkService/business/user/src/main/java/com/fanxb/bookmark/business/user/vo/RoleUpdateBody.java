package com.fanxb.bookmark.business.user.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 类功能简述：编辑角色表单，code 不可修改
 *
 * @author fanxb
 */
@Data
public class RoleUpdateBody {

    @NotNull(message = "角色id不能为空")
    private Integer roleId;

    @NotBlank(message = "角色名称不能为空")
    private String name;

    private String description;
}
