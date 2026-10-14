package com.fanxb.bookmark.common.permission;

import com.fanxb.bookmark.common.dao.RoleDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 类功能简述：悬空权限绑定审计，启动时扫描角色绑定的权限code是否仍存在于注册表
 * 类功能详述：permissions.yml 中删除权限后存量绑定成为悬空引用，解析时静默忽略，此处打 warn 日志提示管理员清理
 *
 * @author fanxb
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PermissionBindingAuditor implements ApplicationRunner {

    private final RoleDao roleDao;

    private final PermissionRegistry permissionRegistry;

    @Override
    public void run(ApplicationArguments args) {
        List<String> codes = roleDao.selectDistinctPermissionCodes();
        if (codes == null) {
            return;
        }
        for (String code : codes) {
            if (!permissionRegistry.permissionExists(code)) {
                log.warn("角色权限绑定存在悬空权限code（permissions.yml中不存在），请到角色管理清理：{}", code);
            }
        }
    }
}
