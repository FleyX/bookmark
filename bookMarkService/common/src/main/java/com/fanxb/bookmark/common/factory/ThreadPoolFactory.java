package com.fanxb.bookmark.common.factory;


import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 类功能简述： 线程池工厂，可使用此工厂创建线程池,等待队列使用：ArrayBlockingQueue
 * 类功能详述：
 *
 * @author fanxb
 * @date 2018/11/1 10:57
 */
public class ThreadPoolFactory {
    /**
     * 默认核心池大小
     */
    public static final int DEFAULT_CORE_POOL_SIZE = 5;
    /**
     * 默认最大线程数
     */
    public static final int DEFAULT_MAX_POOL_SIZE = 30;

    /**
     * 默认空闲线程回收时间（毫秒）
     */
    public static final long DEFAULT_KEEP_ACTIVE_TIME = 1000;

    /**
     * 默认等待队列长度
     */
    public static final int DEFAULT_QUEUE_LENGTH = 2000;

    /**
     * Description: 使用默认配置创建一个连接池
     *
     * @param factoryName 线程工厂名
     * @return java.util.concurrent.ThreadPoolExecutor
     * @author fanxb
     * @date 2018/10/12 13:38
     */
    public static ThreadPoolExecutor createPool(String factoryName) {
        ThreadFactory threadFactory = new CustomThreadFactory(factoryName);
        return createPool(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_POOL_SIZE, DEFAULT_KEEP_ACTIVE_TIME, DEFAULT_QUEUE_LENGTH, threadFactory);
    }

    /**
     * Description: 提供参数创建一个连接池
     *
     * @param corePoolSize   核心池大小
     * @param maxPoolSize    线程池最大线程数
     * @param keepActiveTime 空闲线程回收时间（ms)
     * @param queueLength    等待队列长度
     * @return java.util.concurrent.ThreadPoolExecutor
     * @author fanxb
     * @date 2018/10/12 13:39
     */
    public static ThreadPoolExecutor createPool(int corePoolSize, int maxPoolSize, long keepActiveTime
            , int queueLength, String threadName) {
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepActiveTime
                , TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueLength), new CustomThreadFactory(threadName));

    }

    /**
     * Description: 提供参数创建一个连接池,自己提供线程工厂
     *
     * @param corePoolSize   核心池大小
     * @param maxPoolSize    线程池最大线程数
     * @param keepActiveTime 空闲线程回收时间（ms)
     * @param queueLength    等待队列长度
     * @param factory        线程工厂
     * @return java.util.concurrent.ThreadPoolExecutor
     * @author fanxb
     * @date 2018/10/12 13:39
     */
    public static ThreadPoolExecutor createPool(int corePoolSize, int maxPoolSize, long keepActiveTime
            , int queueLength, ThreadFactory factory) {
        return new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepActiveTime
                , TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueLength), factory);

    }

    /**
     * Description: 强制关闭线程池，不等待已有任务的完成
     *
     * @param executor 被关闭线程池对象
     * @return List<Runnable>
     * @author fanxb
     * @date 2018/10/12 13:42
     */
    public static List<Runnable> forceShutdown(ThreadPoolExecutor executor) {
        if (executor != null) {
            return executor.shutdownNow();
        } else {
            return null;
        }
    }

    /**
     * Description: 关闭一个连接池，等待已有任务完成
     *
     * @param executor 被关闭线程池对象
     * @return void
     * @author fanxb
     * @date 2018/10/12 13:45
     */
    public static void shutdown(ThreadPoolExecutor executor) {
        if (executor == null) {
            return;
        }
        executor.shutdown();
        try {
            int count = 0;
            int timeOut = 2;
            while (executor.awaitTermination(timeOut, TimeUnit.SECONDS)) {
                count++;
                if (count == 100) {
                    executor.shutdownNow();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
