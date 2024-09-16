package top.ezadmin.web.safe;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class IpPeriodRule extends AbstractRule{
    private static List<Cache<String, AtomicLong>> list=new ArrayList<>();
    private SafeCallback callback=null;
    static Integer[][] config=new Integer[][]{{5000,10},{60000,50},{300000,100},{1800000,300},{3600000,400},{7200000,500}};


    public void init(SafeCallback c) {
        callback=c;
        for (int i = 0; i < config.length; i++) {
            Integer[] cg=config[i];
            RemovalListener<String, AtomicLong> removalListener = (key, value, cause) -> {
                if(value.get()>cg[1]){
                    callback.doCallback(key,value.get(),cg[0],cg[1]);
                }
            };
            Cache<String, AtomicLong> IP_LIMIT_CACHE=   Caffeine.newBuilder()
                    .expireAfterWrite(cg[0], TimeUnit.MILLISECONDS)
                    .maximumSize(50000).removalListener(removalListener)
                    .build();
            list.add(IP_LIMIT_CACHE);
        }
        //每隔5s统计一次
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
        Runnable task = () -> {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).cleanUp();
            }
        };
        scheduler.scheduleAtFixedRate(task, 3, 5, TimeUnit.SECONDS);
    }

    @Override
    public boolean onMessage(IpActionDto dto) {
        for (int i = 0; i < list.size(); i++) {
            AtomicLong cc= list.get(i).get(dto.getIp(),k->{
                return new AtomicLong(0);
            });
            cc.incrementAndGet();
        }
        return false;
    }

    public void clear(String ip){
        for (int i = 0; i < list.size(); i++) {
            list.get(i).invalidate(ip);
        }
    }
    public String print(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append("区间拦截器明细:"+config[i][0]+"ms\t<br>"+getIpGroup(list.get(i).asMap().keySet())+"\n<br>");
        }
        return sb.toString();
    }
}
