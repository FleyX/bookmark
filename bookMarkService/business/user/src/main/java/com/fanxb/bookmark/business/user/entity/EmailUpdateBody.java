package com.fanxb.bookmark.business.user.entity;

import com.fanxb.bookmark.business.user.constant.ValidatedConstant;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

/**
 * 类功能简述：修改邮箱表单
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/9/20 16:35
 */
@Data
public class EmailUpdateBody {
    @Pattern(regexp = ValidatedConstant.PASSWORD_REG, message = ValidatedConstant.PASSWORD_MESSAGE)
    private String oldPass;
    @Email
    private String newEmail;
}
