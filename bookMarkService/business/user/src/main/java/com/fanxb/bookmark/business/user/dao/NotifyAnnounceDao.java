package com.fanxb.bookmark.business.user.dao;

import com.fanxb.bookmark.business.user.vo.UserNotifyAnnounceRes;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author fanxb
 * @date 2021-10-17 14:19
 */
public interface NotifyAnnounceDao {

    /**
     * 获取用户站内信
     *
     * @param userId userId
     * @param status status
     * @return java.util.List<com.fanxb.bookmark.business.user.vo.UserNotifyAnnounceRes>
     * @author fanxb
     * @date 2021/10/17 14:28
     */
    List<UserNotifyAnnounceRes> queryUserAnnounce(@Param("userId") int userId, @Param("status") int status, @Param("date") long date);

    /**
     * 处理某人的邮件
     *
     * @param userId userId
     * @author fanxb
     * @date 2021/10/17 15:05
     */
    @Insert("insert into user_notify_announce(userId,notifyAnnounceId,status) select #{userId},notifyAnnounceId,0 from notify_announce a where a.createdDate > (select lastSyncAnnounceDate from user where userId=#{userId})")
    void dealNotifyAnnounceById(int userId);

    /**
     * 标记为已读
     *
     * @param userId           userId
     * @param notifyAnnounceId notifyAnnounceId
     * @author fanxb
     * @date 2021/10/17 15:26
     */
    @Update("update user_notify_announce set status=1,readDate=#{readDate} where userId=#{userId} and notifyAnnounceId=#{notifyAnnounceId}")
    void markAsRead(@Param("userId") int userId, @Param("notifyAnnounceId") int notifyAnnounceId, @Param("readDate") long readDate);
}
