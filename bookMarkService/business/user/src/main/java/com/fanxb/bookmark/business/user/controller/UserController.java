package com.fanxb.bookmark.business.user.controller;

import com.fanxb.bookmark.business.user.entity.LoginBody;
import com.fanxb.bookmark.business.user.entity.RegisterBody;
import com.fanxb.bookmark.business.user.service.UserService;
import com.fanxb.bookmark.common.entity.Result;
import com.fanxb.bookmark.common.util.UserContextHolder;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/4 19:51
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Description: 获取验证码
     *
     * @param email 邮箱
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/7/5 17:37
     */
    @GetMapping("/authCode")
    public Result getAuthCode(@Param("email") String email) {
        userService.sendAuthCode(email);
        return Result.success(null);
    }

    /**
     * Description: 注册用户
     *
     * @param body 注册表单
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/7/6 16:34
     */
    @PutMapping("")
    public Result register(@RequestBody RegisterBody body) {
        userService.register(body);
        return Result.success(null);
    }

    /**
     * Description: 获取当前登录用户的基本信息
     *
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/7/30 15:14
     */
    @GetMapping("/currentUserInfo")
    public Result currentUserInfo() {
        return Result.success(userService.getUserInfo(UserContextHolder.get().getUserId()));
    }


    /**
     * 修改用户头像
     *
     * @param file 头像文件
     */
    @PostMapping("/icon")
    public Result pushIcon(@RequestParam("file") MultipartFile file) throws Exception {
        return Result.success(userService.updateIcon(file));
    }

    /**
     * Description: 用户登录
     *
     * @param body 登录表单
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/7/6 16:35
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginBody body) {
        return Result.success(userService.login(body));
    }

    /**
     * Description: 重置密码
     *
     * @param body 重置密码表单
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/7/9 19:57
     */
    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody RegisterBody body) {
        userService.resetPassword(body);
        return Result.success(null);
    }


}
