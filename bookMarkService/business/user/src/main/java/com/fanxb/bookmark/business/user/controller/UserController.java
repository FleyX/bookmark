package com.fanxb.bookmark.business.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.fanxb.bookmark.business.user.service.OAuthService;
import com.fanxb.bookmark.business.user.service.UserService;
import com.fanxb.bookmark.business.user.vo.LoginBody;
import com.fanxb.bookmark.business.user.vo.OAuthBody;
import com.fanxb.bookmark.business.user.vo.RegisterBody;
import com.fanxb.bookmark.business.user.service.impl.UserServiceImpl;
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

    private final UserServiceImpl userServiceImpl;
    private final OAuthService oAuthService;
    private final UserService userService;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl, OAuthService oAuthService, UserService userService) {
        this.userServiceImpl = userServiceImpl;
        this.oAuthService = oAuthService;
        this.userService = userService;
    }

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
        userServiceImpl.sendAuthCode(email);
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
        return Result.success(userServiceImpl.register(body));
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
        return Result.success(userServiceImpl.getUserInfo(UserContextHolder.get().getUserId()));
    }


    /**
     * 修改用户头像
     *
     * @param file 头像文件
     */
    @PostMapping("/icon")
    public Result pushIcon(@RequestParam("file") MultipartFile file) throws Exception {
        return Result.success(userServiceImpl.updateIcon(file));
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
        return Result.success(userServiceImpl.login(body));
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
        userServiceImpl.resetPassword(body);
        return Result.success(null);
    }

    /**
     * 功能描述: 校验密码，生成一个actionId
     *
     * @param obj obj
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/11/11 23:31
     */
    @PostMapping("/checkPassword")
    public Result checkPassword(@RequestBody JSONObject obj) {
        return Result.success(userServiceImpl.checkPassword(obj.getString("password")));
    }

    @GetMapping("/loginStatus")
    public Result checkLoginStatus() {
        return Result.success(null);
    }

    /**
     * 第三方登陆
     *
     * @param body 入参
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2021/3/10
     */
    @PostMapping("oAuthLogin")
    public Result oAuthLogin(@RequestBody OAuthBody body) {
        return Result.success(oAuthService.oAuthCheck(body));
    }

    /**
     * 获取用户version
     *
     * @date 2021/3/11
     **/
    @GetMapping("/version")
    public Result getUserVersion() {
        return Result.success(userService.getCurrentUserVersion(UserContextHolder.get().getUserId()));
    }

    /**
     * 更新所有人的icon数据
     *
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2021/3/13
     **/
    @PostMapping("/updateAllUserIcon")
    public Result updateAllUserIcon() {
        userService.updateAllUserIcon();
        return Result.success(null);
    }

    /**
     * 处理所有的问题书签数据
     *
     * @param obj obj
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2021/3/17
     **/
    @PostMapping("/dealAllUserBookmark")
    public Result dealAllUserBookmark(@RequestBody JSONObject obj) {
        return Result.success(userService.dealAllUserBookmark(obj.getBoolean("delete")));
    }


}
