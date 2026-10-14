package com.fanxb.bookmark.business.user.vo;

import com.fanxb.bookmark.common.entity.po.RolePo;
import lombok.Data;

import java.util.List;

/**
 * 类功能简述：角色分页响应
 *
 * @author fanxb
 */
@Data
public class RolePageRes {

    private final long total;

    private final List<RolePo> list;
}
