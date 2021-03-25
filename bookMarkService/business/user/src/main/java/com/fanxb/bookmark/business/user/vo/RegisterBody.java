package com.fanxb.bookmark.business.user.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 类功能简述： 注册表单
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/6 11:23
 */
@Data
public class RegisterBody {
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^\\w{1,50}$", message = "用户名长度为1-50")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^\\w{6,18}$", message = "密码为6-18位组合")
    private String password;
    @NotBlank(message = "邮箱不能为空")
    private String email;
    private String authCode;
}
