package com.fanxb.bookmark.common.entity.redis;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBookmarkUpdate {
    /**
     * 用户id
     */
    private int userId;
    /**
     * 更新时间
     */
    private long updateTime;

    public UserBookmarkUpdate(int userId) {
        this.userId = userId;
        this.updateTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
