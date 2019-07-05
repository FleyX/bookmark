package com.fanxb.bookmark.common.util;


import com.fanxb.bookmark.common.factory.ThreadPoolFactory;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 类功能简述：当短时间需要少量线程时,使用本类。长时间占用或大量线程需求，请用线程工厂ThreadPoolFactory创建新的线程池,
 * 类功能详述：
 *
 * @author fanxb
 * @date 2018/11/1 11:04
 */
public class ThreadPoolUtil {
    private static final ThreadPoolExecutor POOL_EXECUTOR = ThreadPoolFactory.createPool("临时线程");

    /**
     * Description: 执行线程任务
     *
     * @param runnable 待执行对象
     * @author fanxb
     * @date 2018/11/1 11:27
     */
    synchronized private static void executeTask(Runnable runnable) {
        POOL_EXECUTOR.execute(runnable);
    }


    /**
     * Description: 执行一个有返回值的任务
     *
     * @param callable 待执行对象
     * @param timeOut  获取结果操时时间 , 操时抛出错误 单位：秒
     * @return T
     * @author fanxb
     * @date 2018/11/1 11:10
     */
    @SuppressWarnings("unchecked")
    public static <T> T execute(Callable<T> callable, int timeOut) throws Exception {
        FutureTask<?> futureTask = new FutureTask<>(callable);
        executeTask(futureTask);
        return (T) futureTask.get(timeOut, TimeUnit.SECONDS);
    }

    /**
     * Description: 执行一个无返回值任务
     *
     * @param runnable 待执行对象
     * @author fanxb
     * @date 2018/11/1 11:11
     */
    public static void execute(Runnable runnable) {
        executeTask(runnable);
    }

}
