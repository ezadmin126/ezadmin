package top.ezadmin.web.safe;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 接受消息，计算次数，异步判断是否拦截
 */
public class UrlLimitRule extends AbstractRule {

    private static final Logger logger = LoggerFactory.getLogger(UrlLimitRule.class);

    private static Cache<String, AtomicLong> URL_LIMIT_CACHE5 = null;
    private static Cache<String, AtomicLong> URL_LIMIT_CACHE60 = null;
    private SafeCallback callback=null;
    public void init(SafeCallback c) {
        callback=c;
        URL_LIMIT_CACHE5 = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .maximumSize(2500)
                .removalListener(new RemovalListener<String, AtomicLong>() {
                    @Override
                    public void onRemoval(String s, AtomicLong atomicLong, RemovalCause removalCause) {
                        if (atomicLong!=null&&atomicLong.get() > 30) {
                             callback.doCallback(s, atomicLong.get(), 5, 30);
                        }
                    }
                })
                .build();

        URL_LIMIT_CACHE60 = Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .maximumSize(2500)
                .removalListener(new RemovalListener<String, AtomicLong>() {
                    @Override
                    public void onRemoval(String s, AtomicLong atomicLong, RemovalCause removalCause) {
                        if (atomicLong!=null&&atomicLong.get() > 100) {
                             callback.doCallback(s, atomicLong.get(), 60, 100);
                        }
                    }
                })
                .build();
        //每隔1分钟统计一次
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
        //5分钟执行一次IP监控
        Runnable task = () -> {
             //   logger.info("UrlLimitRule  定时任务执行");
                URL_LIMIT_CACHE5.cleanUp();
                URL_LIMIT_CACHE60.cleanUp();
        };
        scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);
    }

    public boolean onMessage(IpActionDto dto) {
        String groupPatternUrl=getPatternGroupUrl(dto.getUri());
        if (StringUtils.isNotBlank(groupPatternUrl)) {
            AtomicLong countUrlLimit = URL_LIMIT_CACHE5.get(groupPatternUrl, k -> new AtomicLong(0));
            AtomicLong countUrlLimit60 = URL_LIMIT_CACHE60.get(groupPatternUrl, k -> new AtomicLong(0));
            countUrlLimit.incrementAndGet();
            countUrlLimit60.incrementAndGet();
        }
        return true;
    }
    public String print(){
        StringBuilder result = new StringBuilder();
        result.append("URL拦截器明细5:"+URL_LIMIT_CACHE5.asMap()+"\n<br>");
        result.append("URL拦截器明细60:"+URL_LIMIT_CACHE60.asMap()+"\n<br>");
        return result.toString();
    }
}

