package com.fanxb.bookmark.business.user.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 类功能简述：用户启用/禁用表单
 *
 * @author fanxb
 */
@Data
public class UserStatusBody {

    @NotNull(message = "用户id不能为空")
    private Integer userId;

    @NotNull(message = "用户状态不能为空")
    private Integer status;
}
