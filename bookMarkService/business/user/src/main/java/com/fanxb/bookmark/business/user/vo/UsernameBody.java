package com.fanxb.bookmark.business.user.vo;

import com.fanxb.bookmark.business.user.constant.ValidatedConstant;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/9/20 15:46
 */
@Data
public class UsernameBody {

    @Pattern(regexp = ValidatedConstant.USERNAME_REG, message = ValidatedConstant.USERNAME_MESSAGE)
    private String username;
}
