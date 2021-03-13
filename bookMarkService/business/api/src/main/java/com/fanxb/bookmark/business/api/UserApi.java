package com.fanxb.bookmark.business.api;

public interface UserApi {
    /**
     * 版本自增
     *
     * @param userId 用户id
     */
    void versionPlus(int userId);

    /**
     * 更新所有用户的version
     *
     * @author fanxb
     * @date 2021/3/13
     **/
    void allUserVersionPlus();
}
