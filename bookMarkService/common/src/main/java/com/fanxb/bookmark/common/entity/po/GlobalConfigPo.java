package com.fanxb.bookmark.common.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 全局配置表
 *
 * @author FleyX
 */
@Data
@TableName("global_config")
public class GlobalConfigPo {
    @TableId(value = "code")
    private String code;
    private String value;
    private String description;
}
