package top.ezadmin.ip;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.math.Calculator;
import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Weigher;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import top.ezadmin.common.utils.NumberUtils;
import top.ezadmin.controller.UploadController;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IpWafService {
    static  Logger logger = LoggerFactory.getLogger(IpWafService.class);

    private static String[] ips=new String[1000];
   private static AtomicInteger index=new AtomicInteger(0);

    //10秒内通过的总数
    private static   Cache<String, AtomicInteger> cache= Caffeine.newBuilder()
           .expireAfterWrite(10, TimeUnit.SECONDS)
    .maximumSize(1)
    .build();


    private static List<IpFilter> list=new ArrayList<>();
    private static IpFilter hour=new IpFilter(3600000, 1000  );//0.27
    private static IpFilter half=new IpFilter(1800000, 800 ); //0.44
    private static IpFilter fiveMinute=new IpFilter(300000, 250 ); //0.83
    private static IpFilter oneMinute=new IpFilter(60000, 60  );//60s 96  1秒1个
    private static IpFilter fiveSecond=new IpFilter(5000, 10  );//5s 10 1秒2个


   static {

        list.add(fiveSecond);
        list.add(oneMinute);
        list.add(fiveMinute);
        list.add(half);
        list.add(hour);
    }
    public static void print(){
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).toString());
        }
    }

    public static boolean ipSafe(IpActionDto dto){
       try {

           //纯IP
           ips[index.getAndIncrement() % 1000] = dto.getIp();
           boolean could = true;
           for (int i = 0; i < list.size(); i++) {
               int result = list.get(i).doFilter(dto.getIp(), dto.getT());
               if (result != 1) {
                   could = false;
                   index.set(0);
               }
           }
           //如果是ip池
           if (could) {
               //计算总次数
               AtomicInteger _count = cache.get("_count", k -> {
                   // 初始化
                   return new AtomicInteger(0);
               });
               if (_count.getAndIncrement() > 800) {
                   Map<String, Integer> current=getTopThree();
                   for(Map.Entry<String, Integer> e:current.entrySet()){
                       if( dto.getIp().startsWith(e.getKey())) {
                           logger.error("IP池拦截触发 命中IP池 {} {}", e.getKey(), dto.getIp());
                           return false;
                       }
                   }
               }
           }
           return could;
       }catch (Exception e){
           return true;
       }
    }
    public  static Map<String, Integer> getTopThree(){
        // 使用Stream API进行分组并计数
        Map<String, Integer> groupedNamesCount =new HashMap<>();
        for (int i = 0; i < ips.length; i++) {
            String ip=ips[i];
            if(StringUtils.isNotBlank(ip)){
                try {
                    String pre = StringUtils.substring(ip,0, ip.lastIndexOf("."));
                    if (groupedNamesCount.containsKey(pre)) {
                        groupedNamesCount.put(pre, groupedNamesCount.get(pre) + 1);
                    } else {
                        groupedNamesCount.put(pre, 1);
                    }
                }catch (Exception e){
                    System.out.println(ip);
                    e.printStackTrace();
                }
            }
        }
        return   groupedNamesCount.entrySet().stream()
                .filter(entry -> entry.getValue() > 10)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) // 按照计数从大到小排序
                .limit(3) // 限制结果集大小为前三个
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }




}
