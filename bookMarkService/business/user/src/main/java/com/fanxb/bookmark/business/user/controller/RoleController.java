package com.fanxb.bookmark.business.user.controller;

import com.fanxb.bookmark.business.user.service.RoleService;
import com.fanxb.bookmark.business.user.vo.RoleAddBody;
import com.fanxb.bookmark.business.user.vo.RolePermissionBody;
import com.fanxb.bookmark.business.user.vo.RoleStatusBody;
import com.fanxb.bookmark.business.user.vo.RoleUpdateBody;
import com.fanxb.bookmark.common.entity.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类功能简述：角色管理接口
 * 类功能详述：权限守护由 LoginFilter 统一完成，资源登记见 web 模块 permissions.yml 的 system:role
 *
 * @author fanxb
 */
@RestController
@RequestMapping("/role/manage")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    /**
     * Description: 角色分页列表
     *
     * @param pageNum  页码，从1开始
     * @param pageSize 页大小
     * @param keyword  关键字，模糊匹配 code/name，可为空
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     */
    @GetMapping("/page")
    public Result page(int pageNum, int pageSize, String keyword) {
        return Result.success(roleService.page(pageNum, pageSize, keyword));
    }

    /**
     * Description: 角色详情，含已绑权限code
     *
     * @param roleId 角色id
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     */
    @GetMapping("/detail")
    public Result detail(int roleId) {
        return Result.success(roleService.detail(roleId));
    }

    /**
     * Description: 启用状态角色精简列表，供分配角色下拉使用
     *
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     */
    @GetMapping("/options")
    public Result options() {
        return Result.success(roleService.options());
    }

    /**
     * Description: 权限树，供角色编辑页渲染勾选树
     *
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     */
    @GetMapping("/permissionTree")
    public Result permissionTree() {
        return Result.success(roleService.permissionTree());
    }

    /**
     * Description: 新建角色
     *
     * @param body 表单
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     */
    @PutMapping("")
    public Result add(@Valid @RequestBody RoleAddBody body) {
        roleService.add(body);
        return Result.success();
    }

    /**
     * Description: 编辑角色，code 不可修改
     *
     * @param body 表单
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     */
    @PostMapping("")
    public Result update(@Valid @RequestBody RoleUpdateBody body) {
        roleService.update(body);
        return Result.success();
    }

    /**
     * Description: 启用/禁用角色
     *
     * @param body 表单
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     */
    @PostMapping("/status")
    public Result updateStatus(@Valid @RequestBody RoleStatusBody body) {
        roleService.updateStatus(body.getRoleId(), body.getStatus());
        return Result.success();
    }

    /**
     * Description: 删除角色，有用户绑定时拒绝
     *
     * @param roleId 角色id
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     */
    @DeleteMapping("/{roleId}")
    public Result delete(@PathVariable int roleId) {
        roleService.delete(roleId);
        return Result.success();
    }

    /**
     * Description: 绑定权限，全量替换
     *
     * @param body 表单
     * @return com.fanxb.bookmark.common.entity.Result
     * @author fanxb
     */
    @PostMapping("/permissions")
    public Result bindPermissions(@Valid @RequestBody RolePermissionBody body) {
        roleService.bindPermissions(body.getRoleId(), body.getPermissionCodes());
        return Result.success();
    }
}
