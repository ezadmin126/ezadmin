package top.ezadmin.web.safe;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class DefaultLocalFilter {
    private static final Logger logger = LoggerFactory.getLogger(DefaultLocalFilter.class);

     private static Cache<String, String> BLACKLIST = null;


    public static void init(){
        if(BLACKLIST != null){
            return;
        }
        BLACKLIST=Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(50000)
                .build();

        SafeCallback urllimit=new SafeCallback() {
            @Override
            public void doCallback(String ip, long count, long period, long max) {
                logger.info("反爬告警 URL限流 {} {} {} {}",ip,count,period,max);
                BLACKLIST.put(ip,"URL限流");
            }
        };
        SafeCallback pool=new SafeCallback() {
            @Override
            public void doCallback(String ip, long count, long period, long max) {
                logger.info("反爬告警 IP池 {} {} {} {}",ip,count,period,max);
                BLACKLIST.put(ip,"IP池");
            }
        };
        SafeCallback night=new SafeCallback() {
            @Override
            public void doCallback(String ip, long count, long period, long max) {
                logger.info("反爬告警 异常时间段访问 {} {} {} {}",ip,count,period,max);
                BLACKLIST.put(ip,"异常时间段访问");
            }
        };
        SafeCallback p=new SafeCallback() {
            @Override
            public void doCallback(String ip, long count, long period, long max) {
                logger.info("反爬告警  区间访问 {} {} {} {}",ip,count,period,max);
                BLACKLIST.put(ip,"区间访问");
            }
        };
        RuleHolder.getInstance().init( urllimit,pool,night,p);
    }

    public static boolean isSafe(String url,String ip){
        if(BLACKLIST.getIfPresent(ip)!=null){
            return false;
        }
        IpActionDto ipActionDto = new IpActionDto();
        ipActionDto.setIp(ip );
        ipActionDto.setUri(url);
        RuleHolder.getInstance().onMessage(ipActionDto);
        return true;
    }
}
