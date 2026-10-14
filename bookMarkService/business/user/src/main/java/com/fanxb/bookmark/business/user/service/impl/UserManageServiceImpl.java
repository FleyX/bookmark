package com.fanxb.bookmark.business.user.service.impl;

import com.fanxb.bookmark.business.api.BookmarkApi;
import com.fanxb.bookmark.business.user.dao.UserDao;
import com.fanxb.bookmark.business.user.service.UserManageService;
import com.fanxb.bookmark.business.user.vo.UserDetailRes;
import com.fanxb.bookmark.business.user.vo.UserItemRes;
import com.fanxb.bookmark.business.user.vo.UserPageRes;
import com.fanxb.bookmark.business.user.vo.UserRoleRes;
import com.fanxb.bookmark.common.constant.CommonConstant;
import com.fanxb.bookmark.common.dao.RoleDao;
import com.fanxb.bookmark.common.entity.po.RolePo;
import com.fanxb.bookmark.common.entity.po.User;
import com.fanxb.bookmark.common.exception.CustomException;
import com.fanxb.bookmark.common.exception.FormDataException;
import com.fanxb.bookmark.common.service.PermissionService;
import com.fanxb.bookmark.common.service.UserStateService;
import com.fanxb.bookmark.common.util.HashUtil;
import com.fanxb.bookmark.common.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 类功能简述：用户管理服务实现
 * 类功能详述：禁用与重置密码写用户状态标记吊销旧 JWT；角色变更后主动失效该用户权限缓存
 *
 * @author fanxb
 */
@Service
@RequiredArgsConstructor
public class UserManageServiceImpl implements UserManageService {

    private static final int ENABLE_STATUS = 1;

    private static final int DISABLE_STATUS = 0;

    private static final int MAX_PAGE_SIZE = 100;

    private final UserDao userDao;

    private final RoleDao roleDao;

    private final UserStateService userStateService;

    private final PermissionService permissionService;

    private final BookmarkApi bookmarkApi;

    @Override
    public UserPageRes page(int pageNum, int pageSize, String keyword) {
        int safePageNum = Math.max(pageNum, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), MAX_PAGE_SIZE);
        String trimmedKeyword = StringUtil.isEmpty(keyword) ? null : keyword.trim();
        List<User> users = userDao.selectUserPage(trimmedKeyword, (safePageNum - 1) * safePageSize, safePageSize);
        List<UserItemRes> list = new ArrayList<>();
        if (users != null) {
            for (User user : users) {
                list.add(toItemRes(user));
            }
        }
        return new UserPageRes(userDao.countUser(trimmedKeyword), list);
    }

    @Override
    public UserDetailRes detail(int userId) {
        User user = getUser(userId);
        UserDetailRes res = new UserDetailRes();
        res.setUserId(user.getUserId());
        res.setUsername(user.getUsername());
        res.setEmail(user.getEmail());
        res.setIcon(user.getIcon());
        res.setCreateTime(user.getCreateTime());
        res.setLastLoginTime(user.getLastLoginTime());
        res.setStatus(user.getStatus());
        res.setBookmarkCount(bookmarkApi.countBookmarkByUserId(userId));
        res.setRoles(toRoleResList(roleDao.selectRolesByUserId(userId)));
        return res;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(int userId, int status) {
        if (status != ENABLE_STATUS && status != DISABLE_STATUS) {
            throw new FormDataException("用户状态非法");
        }
        checkNotSuperAdmin(userId);
        getUser(userId);
        userDao.updateStatus(userId, status);
        if (status == DISABLE_STATUS) {
            userStateService.disable(userId);
        } else {
            userStateService.enable(userId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(int userId, List<Integer> roleIds) {
        checkNotSuperAdmin(userId);
        getUser(userId);
        Set<Integer> ids = new LinkedHashSet<>();
        if (roleIds != null) {
            ids.addAll(roleIds);
        }
        for (Integer roleId : ids) {
            if (roleId == null) {
                throw new FormDataException("角色id不能为空");
            }
            RolePo role = roleDao.selectRoleById(roleId);
            if (role == null || role.getStatus() == null || role.getStatus() != ENABLE_STATUS) {
                throw new FormDataException("角色不存在或已禁用：" + roleId);
            }
            //超级管理员角色仅由系统初始化绑定，不允许分配，防止越权
            if (CommonConstant.SUPER_ADMIN_ROLE_CODE.equals(role.getCode())) {
                throw new CustomException("超级管理员角色不允许分配");
            }
        }
        roleDao.deleteUserRoles(userId);
        long now = System.currentTimeMillis();
        for (Integer roleId : ids) {
            roleDao.insertUserRole(userId, roleId, now);
        }
        permissionService.evictUser(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(int userId, String password) {
        checkNotSuperAdmin(userId);
        getUser(userId);
        userDao.updatePasswordByUserId(userId, HashUtil.getPassword(password));
        userStateService.markPasswordReset(userId);
    }

    /**
     * 操作目标为超级管理员时一律拒绝，避免管理后台误操作导致失去超管
     */
    private void checkNotSuperAdmin(int userId) {
        List<String> roleCodes = roleDao.selectRoleCodesByUserId(userId);
        if (roleCodes != null && roleCodes.contains(CommonConstant.SUPER_ADMIN_ROLE_CODE)) {
            throw new CustomException("不能操作超级管理员");
        }
    }

    private User getUser(int userId) {
        User user = userDao.selectByUserIdOrGithubId(userId, null);
        if (user == null) {
            throw new FormDataException("用户不存在");
        }
        return user;
    }

    private UserItemRes toItemRes(User user) {
        UserItemRes res = new UserItemRes();
        res.setUserId(user.getUserId());
        res.setUsername(user.getUsername());
        res.setEmail(user.getEmail());
        res.setIcon(user.getIcon());
        res.setCreateTime(user.getCreateTime());
        res.setLastLoginTime(user.getLastLoginTime());
        res.setStatus(user.getStatus());
        return res;
    }

    private List<UserRoleRes> toRoleResList(List<RolePo> roles) {
        List<UserRoleRes> res = new ArrayList<>();
        if (roles == null) {
            return res;
        }
        for (RolePo role : roles) {
            UserRoleRes item = new UserRoleRes();
            item.setRoleId(role.getRoleId());
            item.setCode(role.getCode());
            item.setName(role.getName());
            res.add(item);
        }
        return res;
    }
}
