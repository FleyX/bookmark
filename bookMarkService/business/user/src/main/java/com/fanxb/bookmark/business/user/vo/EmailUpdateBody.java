package com.fanxb.bookmark.business.user.vo;

import com.fanxb.bookmark.business.user.constant.ValidatedConstant;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * 类功能简述：修改邮箱表单
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/9/20 16:35
 */
@Data
public class EmailUpdateBody {
    @NotNull(message = "参数不为空")
    private String oldPassword;
    @Email(message = "请输入有效邮箱地址")
    private String email;
}
