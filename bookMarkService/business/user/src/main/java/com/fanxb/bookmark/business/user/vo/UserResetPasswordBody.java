package com.fanxb.bookmark.business.user.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 类功能简述：管理员重置用户密码表单
 *
 * @author fanxb
 */
@Data
public class UserResetPasswordBody {

    @NotNull(message = "用户id不能为空")
    private Integer userId;

    @NotBlank(message = "密码不能为空")
    private String password;
}
