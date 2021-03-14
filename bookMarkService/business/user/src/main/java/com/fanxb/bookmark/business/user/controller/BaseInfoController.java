package com.fanxb.bookmark.business.user.controller;

import com.fanxb.bookmark.business.user.service.BaseInfoService;
import com.fanxb.bookmark.business.user.vo.EmailUpdateBody;
import com.fanxb.bookmark.business.user.vo.UpdatePasswordBody;
import com.fanxb.bookmark.business.user.vo.UsernameBody;
import com.fanxb.bookmark.common.entity.Result;
import com.fanxb.bookmark.common.entity.User;
import com.fanxb.bookmark.common.util.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 类功能简述：用户基本信息相关功能
 *
 * @author fanxb
 * @date 2019/9/18 15:53
 */
@RestController
@RequestMapping("/baseInfo")
@Validated
public class BaseInfoController {

    private final BaseInfoService baseInfoService;

    @Autowired
    public BaseInfoController(BaseInfoService baseInfoService) {
        this.baseInfoService = baseInfoService;
    }

    /**
     * Description: 修改密码
     *
     * @param body body
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/9/18 15:49
     */
    @PostMapping("/password")
    public Result changePassword(@Validated @RequestBody UpdatePasswordBody body) {
        this.baseInfoService.changePassword(body);
        return Result.success(null);
    }


    /**
     * Description: 修改用户名
     *
     * @param body body
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/9/18 15:42
     */
    @PostMapping("/username")
    public Result updateUsername(@Validated @RequestBody UsernameBody body) {
        this.baseInfoService.updateUsername(body.getUsername());
        return Result.success(null);
    }

    /**
     * Description: 修改邮箱，还需校验新邮箱
     *
     * @param body body
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/9/18 15:41
     */
    @PostMapping("/email")
    public Result updateEmail(@Validated @RequestBody EmailUpdateBody body) {
        baseInfoService.updateEmail(body);
        return Result.success(null);
    }

    /**
     * 功能描述: 校验邮箱
     *
     * @param secret secret
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     * @date 2019/11/11 23:27
     */
    @GetMapping("/verifyEmail")
    public Result verifyEmail(String secret) {
        baseInfoService.verifyEmail(secret);
        return Result.success(null);
    }

    /**
     * 修改用户默认搜索引擎
     *
     * @author fanxb
     * @date 2021/3/14
     **/
    @PostMapping("/updateSearchEngine")
    public Result updateSearchEngine(@RequestBody User user) {
        user.setUserId(UserContextHolder.get().getUserId());
        baseInfoService.changeDefaultSearchEngine(user);
        return Result.success(null);
    }
}
