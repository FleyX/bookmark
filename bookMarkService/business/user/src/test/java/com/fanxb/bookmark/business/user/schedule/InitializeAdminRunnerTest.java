package com.fanxb.bookmark.business.user.schedule;

import com.fanxb.bookmark.business.api.BookmarkApi;
import com.fanxb.bookmark.business.user.dao.UserDao;
import com.fanxb.bookmark.business.user.service.SearchEngineService;
import com.fanxb.bookmark.business.user.service.UserService;
import com.fanxb.bookmark.business.user.service.impl.UserServiceImpl;
import com.fanxb.bookmark.common.constant.CommonConstant;
import com.fanxb.bookmark.common.dao.RoleDao;
import com.fanxb.bookmark.common.entity.po.User;
import com.fanxb.bookmark.common.exception.CustomException;
import com.fanxb.bookmark.common.service.PermissionService;
import com.fanxb.bookmark.common.util.HashUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * 类功能简述：启动初始化管理员单测
 * 类功能详述：以启动钩子为入口，覆盖非空用户表、dev/非dev密码、控制台输出、超管绑定、默认数据及异常传播
 *
 * @author fanxb
 */
@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class InitializeAdminRunnerTest {

    private static final Pattern INIT_PASSWORD = Pattern.compile("初始密码：([A-Za-z0-9]{8})");

    @Mock
    private UserDao userDao;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private BookmarkApi bookmarkApi;

    @Mock
    private RoleDao roleDao;

    @Mock
    private SearchEngineService searchEngineService;

    @Mock
    private PermissionService permissionService;

    private InitializeAdminRunner runner;

    private boolean originalIsDev;

    @BeforeEach
    void setUp() {
        originalIsDev = CommonConstant.isDev;
        UserServiceImpl userService = new UserServiceImpl(userDao, redisTemplate, bookmarkApi, roleDao, permissionService);
        ReflectionTestUtils.setField(userService, "searchEngineService", searchEngineService);
        runner = new InitializeAdminRunner(userService);
    }

    @AfterEach
    void tearDown() {
        CommonConstant.isDev = originalIsDev;
    }

    @Test
    void testNotEmptyUserTableSkip(CapturedOutput output) {
        when(userDao.countUser(null)).thenReturn(1L);

        runner.run(null);

        verify(userDao).countUser(null);
        verifyNoMoreInteractions(userDao);
        verifyNoInteractions(roleDao, searchEngineService);
        assertFalse(output.getAll().contains("初始化管理员创建成功"));
    }

    @Test
    void testDevCreateAdminWithoutPasswordOutput(CapturedOutput output) {
        CommonConstant.isDev = true;
        mockEmptyTable();
        mockSuperAdminRole();

        runner.run(null);

        User created = captureCreatedUser();
        assertEquals("admin", created.getUsername());
        assertEquals("admin@example.com", created.getEmail());
        assertEquals(UserService.DEFAULT_ICON, created.getIcon());
        assertEquals(HashUtil.getPassword("123456"), created.getPassword());
        assertTrue(created.getCreateTime() > 0);
        assertTrue(created.getLastLoginTime() > 0);
        assertEquals(0, created.getVersion());
        verify(roleDao).insertUserRole(eq(1), eq(3), anyLong());
        verify(searchEngineService).newUserInit(1);
        assertFalse(output.getAll().contains("123456"));
        assertFalse(output.getAll().contains("初始密码"));
    }

    @Test
    void testNonDevCreateAdminAndPrintPasswordOnce(CapturedOutput output) {
        CommonConstant.isDev = false;
        mockEmptyTable();
        mockSuperAdminRole();

        runner.run(null);

        User created = captureCreatedUser();
        Matcher matcher = INIT_PASSWORD.matcher(output.getAll());
        assertTrue(matcher.find());
        String password = matcher.group(1);
        assertTrue(password.matches("[A-Za-z0-9]{8}"));
        assertEquals(HashUtil.getPassword(password), created.getPassword());
        assertTrue(output.getAll().contains("用户名：admin"));
        assertFalse(matcher.find());
        verify(roleDao).insertUserRole(eq(1), eq(3), anyLong());
        verify(searchEngineService).newUserInit(1);
    }

    @Test
    void testSuperAdminRoleMissingFailsStartup(CapturedOutput output) {
        mockEmptyTable();
        when(roleDao.selectRoleIdByCodeForUpdate(CommonConstant.SUPER_ADMIN_ROLE_CODE)).thenReturn(null);

        assertThrows(CustomException.class, () -> runner.run(null));

        verify(userDao).addOne(any(User.class));
        verify(roleDao).selectRoleIdByCodeForUpdate(CommonConstant.SUPER_ADMIN_ROLE_CODE);
        verify(roleDao, never()).insertUserRole(anyInt(), anyInt(), anyLong());
        verifyNoInteractions(searchEngineService);
        assertFalse(output.getAll().contains("初始化管理员创建成功"));
    }

    @Test
    void testDefaultDataInitFailureFailsStartup(CapturedOutput output) {
        mockEmptyTable();
        mockSuperAdminRole();
        doThrow(new CustomException("默认数据初始化失败")).when(searchEngineService).newUserInit(1);

        assertThrows(CustomException.class, () -> runner.run(null));

        verify(roleDao).insertUserRole(eq(1), eq(3), anyLong());
        assertFalse(output.getAll().contains("初始化管理员创建成功"));
    }

    private void mockEmptyTable() {
        when(userDao.countUser(null)).thenReturn(0L);
        doAnswer(invocation -> {
            invocation.getArgument(0, User.class).setUserId(1);
            return null;
        }).when(userDao).addOne(any(User.class));
    }

    private void mockSuperAdminRole() {
        when(roleDao.selectRoleIdByCodeForUpdate(CommonConstant.SUPER_ADMIN_ROLE_CODE)).thenReturn(3);
    }

    private User captureCreatedUser() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDao).addOne(captor.capture());
        return captor.getValue();
    }
}
