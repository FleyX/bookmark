package com.fanxb.bookmark.common.configuration;

import com.fanxb.bookmark.common.annotation.MqConsumer;
import com.fanxb.bookmark.common.entity.redis.RedisConsumer;
import com.fanxb.bookmark.common.factory.ThreadPoolFactory;
import com.fanxb.bookmark.common.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2020/3/24
 * Time: 15:37
 */
@Component
@Slf4j
public class MqConfiguration implements ApplicationRunner, DisposableBean {
    /**
     * 是否运行
     */
    private static volatile boolean isRun = true;

    /**
     * 订阅对象与执行方法关系（支持广播模式）
     */
    private static final Map<String, List<RedisConsumer>> topicMap = new HashMap<>();
    /**
     * 执行线程池
     */
    private static final ThreadPoolExecutor threadPoolExecutor = ThreadPoolFactory.createPool(2, 8, 5000, 1000, "mqConsumer");

    @Autowired
    ApplicationContext context;

    @Override
    public void run(ApplicationArguments args) {
        Map<String, Object> map = context.getBeansWithAnnotation(MqConsumer.class);
        map.values().forEach(item -> {
            if (!(item instanceof RedisConsumer)) {
                log.warn("注意检测到被@EsConsumer注解的类{}未实现RedisConsumer接口", item.getClass().getCanonicalName());
                return;
            }
            MqConsumer[] annotations = item.getClass().getAnnotationsByType(MqConsumer.class);
            MqConsumer annotation = annotations[0];
            topicMap.computeIfAbsent(annotation.value(), k -> new ArrayList<>()).add((RedisConsumer) item);
        });
        log.info("redis订阅信息汇总完毕！！！！！！");
        //由一个线程始终循环获取es队列数据
        threadPoolExecutor.execute(loop());
    }

    @Override
    public void destroy() {
        log.info("进程结束，关闭redis");
        isRun = false;
        ThreadPoolFactory.shutdown(threadPoolExecutor);
    }

    private Runnable loop() {
        return () -> {
            while (isRun) {
                AtomicInteger count = new AtomicInteger(0);
                topicMap.forEach((k, v) -> {
                    try {
                        String message = RedisUtil.redisTemplate.opsForList().rightPop(k);
                        if (message == null) {
                            count.getAndIncrement();
                        } else {
                            pushTask(v, message, k);
                        }
                    } catch (Exception e) {
                        log.error("redis消息队列异常", e);
                    }
                });
                if (count.get() == topicMap.keySet().size()) {
                    //当所有的队列都为空时休眠3s
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (Exception e) {
                        log.error("休眠出错", e);
                    }
                }
            }
        };
    }

    /**
     * 功能描述: 推送任务到线程池中执行
     *
     * @param list  list
     * @param value value
     * @param key   key
     * @author 123
     * @date 2020/3/28 23:52
     */
    private void pushTask(List<RedisConsumer> list, String value, String key) {
        for (RedisConsumer consumer : list) {
            threadPoolExecutor.execute(() -> {
                try {
                    consumer.deal(value);
                } catch (Exception e) {
                    log.error("执行消费任务出错", e);
                    if (list.size() == 1) {
                        //非广播消息进行数据回补
                        RedisUtil.redisTemplate.opsForList().rightPush(key, value);
                    }
                }
            });
        }
    }
}

