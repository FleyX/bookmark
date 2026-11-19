package com.fanxb.bookmark.business.user.schedule;

import com.fanxb.bookmark.business.user.service.UserService;
import com.fanxb.bookmark.common.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 类功能简述：启动时初始化管理员
 * 类功能详述：用户表为空时创建初始化管理员；非dev环境首次创建成功后输出一次初始密码
 *
 * @author fanxb
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class InitializeAdminRunner implements ApplicationRunner {

    private final UserService userService;

    @Override
    public void run(ApplicationArguments args) {
        String password = userService.initializeAdmin();
        if (!StringUtil.isEmpty(password)) {
            log.info("初始化管理员创建成功，用户名：admin，初始密码：{}，该密码仅显示一次，请登录后及时修改", password);
        }
    }
}
