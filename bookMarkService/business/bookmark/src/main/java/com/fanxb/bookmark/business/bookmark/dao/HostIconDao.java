package com.fanxb.bookmark.business.bookmark.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author fanxb
 */
@Mapper
public interface HostIconDao {

    /**
     * 插入一条数据
     *
     * @param host     host
     * @param iconPath path
     * @author fanxb
     */
    @Insert("insert into host_icon(host,iconPath) value(#{host},#{iconPath})")
    void insert(@Param("host") String host, @Param("iconPath") String iconPath);

    /**
     * 根据host获取iconPath
     *
     * @param host host
     * @return {@link String}
     * @author fanxb
     */
    @Select("select iconPath from host_icon where host=#{host}")
    String selectByHost(String host);
}
