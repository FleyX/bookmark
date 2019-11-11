package com.fanxb.bookmark.business.user.entity;

import com.fanxb.bookmark.business.user.constant.ValidatedConstant;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * 类功能简述：修改密码表单
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/9/20 16:13
 */
@Data
public class UpdatePasswordBody {

    @Pattern(regexp = ValidatedConstant.PASSWORD_REG, message = ValidatedConstant.PASSWORD_MESSAGE)
    private String oldPass;
    @Pattern(regexp = ValidatedConstant.PASSWORD_REG, message = ValidatedConstant.PASSWORD_MESSAGE)
    private String newPass;
}
