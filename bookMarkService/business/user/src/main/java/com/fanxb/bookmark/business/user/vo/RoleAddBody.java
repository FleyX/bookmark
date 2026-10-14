package com.fanxb.bookmark.business.user.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 类功能简述：新建角色表单
 *
 * @author fanxb
 */
@Data
public class RoleAddBody {

    @NotBlank(message = "角色编码不能为空")
    private String code;

    @NotBlank(message = "角色名称不能为空")
    private String name;

    private String description;
}
