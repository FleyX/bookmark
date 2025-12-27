package com.fanxb.bookmark.common.service;

/**
 * log user use system time
 */
public interface LoginUserLogService {
    /**
     * log to redis
     */
    public void logToRedis();

    /**
     * save to db
     */
    public void saveToDb();
}
