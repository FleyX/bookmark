package com.fanxb.bookmark.business.user.dao;

import com.fanxb.bookmark.business.user.entity.Feedback;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2020/3/10
 * Time: 23:14
 */
public interface FeedbackDao {

    /**
     * 功能描述: 插入一条记录
     *
     * @param feedback feedback
     * @author fanxb
     * @date 2020/3/10 23:16
     */
    @Insert("insert into feedback(userId,type,content) value(#{userId},#{type},#{content})")
    void insertOne(Feedback feedback);
}
