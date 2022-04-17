package com.fanxb.bookmark.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fanxb.bookmark.common.entity.po.GlobalConfigPo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author fanxb
 */
@Mapper
public interface GlobalConfigDao extends BaseMapper<GlobalConfigPo> {
}
