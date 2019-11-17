package com.fanxb.bookmark.business.user.service;

import com.fanxb.bookmark.business.user.constant.FileConstant;
import com.fanxb.bookmark.business.user.dao.UserDao;
import com.fanxb.bookmark.business.user.entity.LoginBody;
import com.fanxb.bookmark.business.user.entity.LoginRes;
import com.fanxb.bookmark.business.user.entity.RegisterBody;
import com.fanxb.bookmark.common.constant.Constant;
import com.fanxb.bookmark.common.constant.NumberConstant;
import com.fanxb.bookmark.common.constant.RedisConstant;
import com.fanxb.bookmark.common.entity.MailInfo;
import com.fanxb.bookmark.common.entity.User;
import com.fanxb.bookmark.common.exception.FormDataException;
import com.fanxb.bookmark.common.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/5 17:39
 */
@Service
public class UserService {

    private static final String DEFAULT_ICON = "defaultIcon.png";
    /**
     * 短期jwt失效时间
     */
    private static final long SHORT_EXPIRE_TIME = 2 * 60 * 60 * 1000;
    /**
     * 长期jwt失效时间
     */
    private static final long LONG_EXPIRE_TIME = 30L * TimeUtil.DAY_MS;

    /**
     * 头像文件大小限制 单位：KB
     */
    private static final int ICON_SIZE = 200;

    @Autowired
    private UserDao userDao;

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
    public void register(RegisterBody body) {
        String codeKey = Constant.authCodeKey(body.getEmail());
        String realCode = RedisUtil.get(codeKey, String.class);
        if (Constant.isDev) {
            realCode = "123456";
        }
        if (StringUtil.isEmpty(realCode) || (!realCode.equals(body.getAuthCode()))) {
            throw new FormDataException("验证码错误");
        }
        RedisUtil.delete(codeKey);
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
        user.setLastLoginTime(0);
        userDao.addOne(user);
    }

    /**
     * Description: 登录
     *
     * @param body 登录表单
     * @return LoginRes
     * @author fanxb
     * @date 2019/7/6 16:37
     */
    public LoginRes login(LoginBody body) {
        User userInfo = userDao.selectByUsernameOrEmail(body.getStr(), body.getStr());
        if (userInfo == null) {
            throw new FormDataException("账号/密码错误");
        }
        if (!HashUtil.sha1(HashUtil.md5(body.getPassword())).equals(userInfo.getPassword())) {
            throw new FormDataException("账号/密码错误");
        }
        Map<String, String> data = new HashMap<>(1);
        data.put("userId", String.valueOf(userInfo.getUserId()));
        String token = JwtUtil.encode(data, Constant.jwtSecret, body.isRememberMe() ? LONG_EXPIRE_TIME : SHORT_EXPIRE_TIME);
        LoginRes res = new LoginRes();
        res.setToken(token);
        res.setUserId(userInfo.getUserId());
        res.setUsername(userInfo.getUsername());
        res.setEmail(userInfo.getEmail());
        res.setIcon(userInfo.getIcon());
        userDao.updateLastLoginTime(System.currentTimeMillis(), userInfo.getUserId());
        return res;
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
        return userDao.selectByUserId(userId);
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
        User user = userDao.selectByUserId(userId);
        if (!user.getPassword().equals(pass)) {
            throw new FormDataException("密码错误,请重试");
        }
        String actionId = UUID.randomUUID().toString().replaceAll("-", "");
        String key = RedisConstant.getPasswordCheckKey(userId, actionId);
        RedisUtil.set(key, "1", 5 * 60 * 1000);
        return actionId;
    }
}
