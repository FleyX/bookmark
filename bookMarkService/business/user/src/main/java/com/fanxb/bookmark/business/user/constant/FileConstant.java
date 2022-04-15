package com.fanxb.bookmark.business.user.constant;

import com.fanxb.bookmark.common.constant.CommonConstant;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/9/9 14:30
 */
@Component
public class FileConstant {

    /**
     * 用户头像目录
     */
    public static String iconPath = Paths.get(CommonConstant.fileSavePath, "files", "public", "icon").toString();

}
