package com.fanxb.bookmark.business.user.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.fanxb.bookmark.business.api.BookmarkApi;
import com.fanxb.bookmark.business.user.constant.FileConstant;
import com.fanxb.bookmark.business.user.dao.UserDao;
import com.fanxb.bookmark.business.user.service.UserService;
import com.fanxb.bookmark.business.user.vo.LoginBody;
import com.fanxb.bookmark.business.user.vo.LoginRes;
import com.fanxb.bookmark.business.user.vo.RegisterBody;
import com.fanxb.bookmark.common.constant.Constant;
import com.fanxb.bookmark.common.constant.NumberConstant;
import com.fanxb.bookmark.common.constant.RedisConstant;
import com.fanxb.bookmark.common.entity.MailInfo;
import com.fanxb.bookmark.common.entity.User;
import com.fanxb.bookmark.common.exception.CustomException;
import com.fanxb.bookmark.common.exception.FormDataException;
import com.fanxb.bookmark.common.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/5 17:39
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final StringRedisTemplate redisTemplate;
    private final BookmarkApi bookmarkApi;

    @Autowired
    public UserServiceImpl(UserDao userDao, StringRedisTemplate redisTemplate, BookmarkApi bookmarkApi) {
        this.userDao = userDao;
        this.redisTemplate = redisTemplate;
        this.bookmarkApi = bookmarkApi;
    }

    /**
     * Description: 向目标发送验证码
     *
     * @param email 目标
     * @author fanxb
     * @date 2019/7/5 17:48
     */
    public void sendAuthCode(String email) {
        MailInfo info = new MailInfo();
        info.setSubject("签签世界注册验证码");
        String code = StringUtil.getRandomString(6, 2);
        info.setContent("欢迎注册 签签世界 ，本次验证码");
        info.setContent(code + " 是您的验证码，注意验证码有效期为15分钟哦！");
        info.setReceiver(email);
        if (Constant.isDev) {
            code = "123456";
        } else {
            MailUtil.sendTextMail(info);
        }
        RedisUtil.set(Constant.authCodeKey(email), code, Constant.AUTH_CODE_EXPIRE);
    }

    /**
     * Description: 用户注册
     *
     * @param body 注册表单
     * @author fanxb
     * @date 2019/7/6 11:30
     */
    public String register(RegisterBody body) {
        User user = userDao.selectByUsernameOrEmail(body.getUsername(), body.getEmail());
        if (user != null) {
            if (user.getUsername().equals(body.getUsername())) {
                throw new FormDataException("用户名已经被注册");
            }
            if (user.getEmail().equals(body.getEmail())) {
                throw new FormDataException("邮箱已经被注册");
            }
        }
        user = new User();
        user.setUsername(body.getUsername());
        user.setEmail(body.getEmail());
        user.setIcon(DEFAULT_ICON);
        user.setPassword(HashUtil.sha1(HashUtil.md5(body.getPassword())));
        user.setCreateTime(System.currentTimeMillis());
        user.setLastLoginTime(System.currentTimeMillis());
        user.setVersion(0);
        userDao.addOne(user);
        Map<String, String> data = new HashMap<>(1);
        data.put("userId", String.valueOf(user.getUserId()));
        return JwtUtil.encode(data, Constant.jwtSecret, LONG_EXPIRE_TIME);
    }

    /**
     * Description: 登录
     *
     * @param body 登录表单
     * @return string
     * @author fanxb
     * @date 2019/7/6 16:37
     */
    public String login(LoginBody body) {
        String key = RedisConstant.getUserFailCountKey(body.getStr());
        String count = redisTemplate.opsForValue().get(key);
        if (count != null && Integer.parseInt(count) >= 5) {
            redisTemplate.expire(key, 30, TimeUnit.MINUTES);
            throw new FormDataException("您已连续输错密码5次，请30分钟后再试，或联系管理员处理");
        }
        User userInfo = userDao.selectByUsernameOrEmail(body.getStr(), body.getStr());
        if (userInfo == null || StrUtil.isEmpty(userInfo.getPassword()) || !HashUtil.sha1(HashUtil.md5(body.getPassword())).equals(userInfo.getPassword())) {
            redisTemplate.opsForValue().set(key, count == null ? "1" : String.valueOf(Integer.parseInt(count) + 1), 30, TimeUnit.MINUTES);
            throw new FormDataException("账号密码错误");
        }
        redisTemplate.delete(key);
        userDao.updateLastLoginTime(System.currentTimeMillis(), userInfo.getUserId());
        return JwtUtil.encode(Collections.singletonMap("userId", String.valueOf(userInfo.getUserId())), Constant.jwtSecret
                , body.isRememberMe() ? LONG_EXPIRE_TIME : SHORT_EXPIRE_TIME);
    }

    /**
     * Description: 重置密码
     *
     * @param body 重置密码 由于参数和注册差不多，所以用同一个表单
     * @author fanxb
     * @date 2019/7/9 19:59
     */
    public void resetPassword(RegisterBody body) {
        User user = userDao.selectByUsernameOrEmail(body.getEmail(), body.getEmail());
        if (user == null) {
            throw new FormDataException("用户不存在");
        }
        String codeKey = Constant.authCodeKey(body.getEmail());
        String realCode = RedisUtil.get(codeKey, String.class);
        if (StringUtil.isEmpty(realCode) || (!realCode.equals(body.getAuthCode()))) {
            throw new FormDataException("验证码错误");
        }
        RedisUtil.delete(codeKey);
        String newPassword = HashUtil.getPassword(body.getPassword());
        userDao.resetPassword(newPassword, body.getEmail());
    }

    /**
     * Description: 根据userId获取用户信息
     *
     * @param userId userId
     * @return com.fanxb.bookmark.common.entity.User
     * @author fanxb
     * @date 2019/7/30 15:57
     */
    public User getUserInfo(int userId) {
        User user = userDao.selectByUserIdOrGithubId(userId, null);
        user.setNoPassword(StrUtil.isEmpty(user.getPassword()));
        return user;
    }

    /**
     * 修改用户头像
     *
     * @param file file
     * @return 访问路径
     */
    public String updateIcon(MultipartFile file) throws Exception {
        if (file.getSize() / NumberConstant.K_SIZE > ICON_SIZE) {
            throw new FormDataException("文件大小超过限制");
        }
        int userId = UserContextHolder.get().getUserId();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        String path = Paths.get(FileConstant.iconPath, userId + "." + System.currentTimeMillis() + fileName.substring(fileName.lastIndexOf("."))).toString();
        Path realPath = Paths.get(Constant.fileSavePath, path);
        FileUtil.ensurePathExist(realPath.getParent().toString());
        file.transferTo(realPath);
        path = File.separator + path;
        userDao.updateUserIcon(userId, path);
        return path;
    }

    /**
     * 功能描述: 密码校验，校验成功返回一个actionId，以执行敏感操作
     *
     * @param password password
     * @return java.lang.String
     * @author fanxb
     * @date 2019/11/11 23:41
     */
    public String checkPassword(String password) {
        int userId = UserContextHolder.get().getUserId();
        String pass = HashUtil.getPassword(password);
        User user = userDao.selectByUserIdOrGithubId(userId, null);
        if (!user.getPassword().equals(pass)) {
            throw new FormDataException("密码错误,请重试");
        }
        String actionId = UUID.randomUUID().toString().replaceAll("-", "");
        String key = RedisConstant.getPasswordCheckKey(userId, actionId);
        RedisUtil.set(key, "1", 5 * 60 * 1000);
        return actionId;
    }

    @Override
    public String createNewUsername() {
        while (true) {
            String name = RandomUtil.randomString(8);
            if (!userDao.usernameExist(name)) {
                return name;
            }
        }
    }

    @Override
    public int getCurrentUserVersion(int userId) {
        return userDao.getUserVersion(userId);
    }

    @Override
    public void updateAllUserIcon() {
        if (!UserContextHolder.get().getManageUser()) {
            throw new CustomException("非管理员用户，无法执行操作");
        }
        ThreadPoolUtil.execute(() -> {
            log.info("开始更新所有人icon");
            int start = 0, size = 1000;
            List<Integer> ids;
            while ((ids = userDao.selectUserIdPage(start, size)).size() > 0) {
                start += size;
                ids.forEach(bookmarkApi::updateUserBookmarkIcon);
            }
            log.info("结束更新所有人icon");
        });
    }

    @Override
    public Map<Integer, Set<String>> dealAllUserBookmark(boolean delete) {
        if (!UserContextHolder.get().getManageUser()) {
            throw new CustomException("非管理员用户，无法执行操作");
        }
        log.info("开始处理所有问题书签数据");
        int start = 0, size = 1000;
        List<Integer> ids;
        Map<Integer, Set<String>> res = new HashMap<>(1000);
        while ((ids = userDao.selectUserIdPage(start, size)).size() > 0) {
            start += size;
            ids.forEach(id -> {
                Set<String> oneUser = bookmarkApi.dealBadBookmark(delete, id);
                if (oneUser.size() > 0) {
                    res.put(id, oneUser);
                }
            });
        }
        log.info("处理完毕");
        return res;
    }
}
