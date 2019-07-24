package com.fanxb.bookmark.common.entity;

import lombok.Data;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 * @date 2019/7/24 17:37
 */
@Data
public class EsInsertEntity<T> {

    private String id;
    private T data;

    public EsInsertEntity() {
    }

    public EsInsertEntity(String id, T data) {
        this.data = data;
        this.id = id;
    }
}
