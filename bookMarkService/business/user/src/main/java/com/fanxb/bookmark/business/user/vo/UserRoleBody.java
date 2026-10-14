package com.fanxb.bookmark.business.user.vo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 类功能简述：用户分配角色表单，全量替换
 *
 * @author fanxb
 */
@Data
public class UserRoleBody {

    @NotNull(message = "用户id不能为空")
    private Integer userId;

    /**
     * 角色id列表，空列表表示清空绑定
     */
    private List<Integer> roleIds;
}
