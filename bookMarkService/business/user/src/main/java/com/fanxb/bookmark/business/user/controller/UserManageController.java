package com.fanxb.bookmark.business.user.controller;

import com.fanxb.bookmark.business.user.service.UserManageService;
import com.fanxb.bookmark.business.user.vo.UserResetPasswordBody;
import com.fanxb.bookmark.business.user.vo.UserRoleBody;
import com.fanxb.bookmark.business.user.vo.UserStatusBody;
import com.fanxb.bookmark.common.entity.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类功能简述：用户管理接口
 * 类功能详述：权限守护由 LoginFilter 统一完成，资源登记见 web 模块 permissions.yml 的 system:user
 *
 * @author fanxb
 */
@RestController
@RequestMapping("/user/manage")
@RequiredArgsConstructor
public class UserManageController {

    private final UserManageService userManageService;

    /**
     * Description: 用户分页列表
     *
     * @param pageNum  页码，从1开始
     * @param pageSize 页大小
     * @param keyword  关键字，模糊匹配 username/email，可为空
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     */
    @GetMapping("/page")
    public Result page(int pageNum, int pageSize, String keyword) {
        return Result.success(userManageService.page(pageNum, pageSize, keyword));
    }

    /**
     * Description: 用户详情，含书签数与已绑角色
     *
     * @param userId 用户id
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     */
    @GetMapping("/detail")
    public Result detail(int userId) {
        return Result.success(userManageService.detail(userId));
    }

    /**
     * Description: 启用/禁用用户
     *
     * @param body 表单
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     */
    @PostMapping("/status")
    public Result updateStatus(@Valid @RequestBody UserStatusBody body) {
        userManageService.updateStatus(body.getUserId(), body.getStatus());
        return Result.success();
    }

    /**
     * Description: 分配角色，全量替换
     *
     * @param body 表单
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     */
    @PostMapping("/roles")
    public Result assignRoles(@Valid @RequestBody UserRoleBody body) {
        userManageService.assignRoles(body.getUserId(), body.getRoleIds());
        return Result.success();
    }

    /**
     * Description: 重置用户密码
     *
     * @param body 表单
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     */
    @PostMapping("/resetPassword")
    public Result resetPassword(@Valid @RequestBody UserResetPasswordBody body) {
        userManageService.resetPassword(body.getUserId(), body.getPassword());
        return Result.success();
    }
}
