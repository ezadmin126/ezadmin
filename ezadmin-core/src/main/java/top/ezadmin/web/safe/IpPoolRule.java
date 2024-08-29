package top.ezadmin.web.safe;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class IpPoolRule extends AbstractRule {
    private static final Logger logger = LoggerFactory.getLogger(IpPoolRule.class);
    private SafeCallback callback=null;

    private static Cache<String, Integer> IP_QUEUE_CACHE=null;
    private static    AtomicLong index=new AtomicLong(0);
    @Override
    public boolean onMessage(IpActionDto dto) {
//        IP_QUEUE_CACHE.get(dto.getIp()+"_"+index.getAndIncrement(),k->{
//            return 1;
//        });
        IP_QUEUE_CACHE.put(dto.getIp()+"_"+index.getAndIncrement(),1);
        return false;
    }


    public void init(SafeCallback c) {
        callback=c;
        if(IP_QUEUE_CACHE!=null) {
            throw new IllegalStateException("IpPoolRule has already been initialized");
        }
        IP_QUEUE_CACHE= Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(2500)
                .build();
        //每隔1分钟统计一次
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

        //  ExecutorService executor = Executors.newFixedThreadPool(1);
        Runnable task = () -> {
            logger.info("IpPoolRule  定时任务执行");
            doIpPool("定时任务");
        };
        scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);
    }
    /**
     * 计算IP池里面的数据，异常流量直接拦截,这里不会拦截单个IP的情况，只有前三位超过指定次数才拦截，单个的使用其他方式。
     * 职责隔离
     * @param type
     */
    void doIpPool(String type){
        Set<String> s= IP_QUEUE_CACHE.asMap().keySet();
        Map<String, Integer> top=getTopThree(s);//ip前3位  大于50次
        if(top.isEmpty()){
            return;
        }
        //把前几个IP前缀的具体IP统计出来，再打印日志。
        Map<String,Set<String>> ipCountMap=new HashMap<>();
        for (String cip:s){
            String pre = StringUtils.substring(cip,0, cip.lastIndexOf("."));//ip前3位
            String removeSeq = StringUtils.substring(cip,0, cip.lastIndexOf("_"));//ip前4位
            if(top.containsKey(pre)){//每个iP前缀 只取10个IP来报错
                if(!ipCountMap.containsKey(pre)){
                    ipCountMap.put(pre,new HashSet<String>());
                }
                if(ipCountMap.get(pre).size()<10){
                    ipCountMap.get(pre).add(removeSeq);
                }
                // addBlack(removeSeq,10L,"IP池超过次数");
            }
        }
        for(Map.Entry<String,Integer> entry:top.entrySet()){
            //拦截新进来的
            if(ipCountMap.containsKey(entry.getKey())){ //只有大于2个不同IP才触发 &&ipCountMap.get(entry.getKey()).size()>1
                callback.doCallback(entry.getKey(),Long.valueOf(ipCountMap.get(entry.getKey()).size()),60000,10);
              //  addBlackTenSecond(entry.getKey(),Long.valueOf(ipCountMap.get(entry.getKey()).size()),"触发IP池统计"+type+",iplist.top10:"+ipCountMap.get(entry.getKey()));
            }
        }
        IP_QUEUE_CACHE.invalidateAll();
    }



    public   Map<String, Integer> getTopThree(Set<String> set){
        // 使用Stream API进行分组并计数
        Map<String, Integer> groupedNamesCount =new HashMap<>();
        if(set==null||set.isEmpty()){
            return groupedNamesCount;
        }
        String[]ips=set.toArray(new String[]{});
        for (int i = 0; i < ips.length; i++) {
            String ip=ips[i];
            if(StringUtils.isNotBlank(ip)){
                try {
                    String pre = StringUtils.substring(ip,0, ip.lastIndexOf("."));
                    //前3位IP
                    if (groupedNamesCount.containsKey(pre)) {
                        groupedNamesCount.put(pre, groupedNamesCount.get(pre) + 1);
                    } else {
                        groupedNamesCount.put(pre, 1);
                    }
                }catch (Exception e){
                    logger.error(ip,e);
                }
            }
        }
        return   groupedNamesCount.entrySet().stream()
                .filter(entry -> entry.getValue() > 50)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) // 按照计数从大到小排序
                .limit(3) // 限制结果集大小为前三个
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    public void clear(String ip){

    }
    public String print(){
        StringBuilder sb = new StringBuilder();
        sb.append("IP池拦截器明细:"+getIpGroup(IP_QUEUE_CACHE.asMap().keySet())+"\n<br>");
       // sb.append("IP池拦截器:"+getIpGroup(global.get(0).getCache().asMap().keySet())+"\n<br>");
         return sb.toString();
    }

}
