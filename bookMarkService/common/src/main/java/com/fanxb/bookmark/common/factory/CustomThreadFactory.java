package com.fanxb.bookmark.common.factory;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * 类功能简述：
 * 类功能详述：线程工厂
 *
 * @author: fanxb
 * @date: 2018/10/12 11:29
 */
public class CustomThreadFactory implements ThreadFactory {

    /**
     * 记录创建线程数
     */
    private int counter;
    /**
     * 线程工程名
     */
    private String name;
    /**
     * 记录最近1000条创建历史
     */
    private List<String> history;

    private int historyLength;

    public CustomThreadFactory(String name) {
        this.name = name;
        this.history = new LinkedList<>();
        this.historyLength = 1000;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, name + "-Thread-" + counter);
        this.counter++;
        history.add(String.format("Created thread %d with name %s on %s \n", t.getId(), t.getName(), new Date().toString()));
        if (history.size() >= this.historyLength) {
            history.remove(0);
        }
        return t;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        this.history.forEach(builder::append);
        return builder.toString();
    }
}
