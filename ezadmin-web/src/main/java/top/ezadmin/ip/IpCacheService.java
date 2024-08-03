package top.ezadmin.ip;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class IpCacheService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IpCacheService.class);

    //10秒
    private static  long window=10000L;
    //100次
    private static  long max=100L;

    private static  int MAX_CACHE_IP_SIZE=5000;
    private static  Long[][] CONFIG_WINDOW_MAX_SIZE=new Long[][]{
            {3600000L, 1800L },
            {1800000L, 1350L},
            {300000L, 300L},
            {60000L, 120L},//60秒120个请求
            {5000L, 20L}};//5秒20个请求
    private static AtomicBoolean INITED=new AtomicBoolean(false);
    private static List<Map<String,Object>> list=new ArrayList<>();
    private static List<Map<String,Object>> global=new ArrayList<>();

    private static   Cache<String, Integer> IP_QUEUE_CACHE=null;
    private static  Cache<String, AtomicInteger> BLACK_MAP=null;
    //验证码模式，需要单独处理
    ConcurrentHashMap<String,Long> whiteMap=new ConcurrentHashMap<>();

    private static BlockingDeque<Map<String,String>  > IP_VISIT_QUEUE=new LinkedBlockingDeque();

    public static boolean isBlack(String ip){
        try {
            int lastend = Math.max(ip.lastIndexOf("."), ip.lastIndexOf(":"));
            String preIp = ip.substring(0, lastend);
            return BLACK_MAP.getIfPresent(ip) != null || BLACK_MAP.getIfPresent(preIp) != null;
        }catch (Exception e){
            LOGGER.error(ip,e);
            return false;
        }
    }
    //json格式的 ip  url param
    public static void monitor(String ip,String url,String param){
        Map<String,String> request=new HashMap<>();
        request.put("ip",ip);
        request.put("url",url);
        request.put("param",param);
        IP_VISIT_QUEUE.addLast(request);
    }
    private static void initFilterList(){
        for (int i = 0; i < CONFIG_WINDOW_MAX_SIZE.length; i++) {
            Map<String,Object> map=new HashMap<>();
            map.put("window",CONFIG_WINDOW_MAX_SIZE[i][0]);
            map.put("max",CONFIG_WINDOW_MAX_SIZE[i][1]);
            final Long curMax=CONFIG_WINDOW_MAX_SIZE[i][1];
            RemovalListener<String, AtomicLong> removalListener = (key, value, cause) -> {
                    if (value.get() > curMax) {
                        //加入黑名单
                        addBlack(key,"超过指定次数"+map.get("window")+",max="+map.get("max")+",cur="+value.get());

                    }
            };
           Cache<String, AtomicLong>  cache= Caffeine.newBuilder()
                    .expireAfterWrite(CONFIG_WINDOW_MAX_SIZE[i][0], TimeUnit.MILLISECONDS)
                    .maximumSize(MAX_CACHE_IP_SIZE).removalListener(removalListener)
                    .build();
            map.put("cache",cache);
            list.add(map);
        }
        Map<String,Object> map=new HashMap<>();
        map.put("window",window);
        map.put("max",max);
        final Long curMax=max;
        RemovalListener<String, AtomicLong> removalListener = (key, value, cause) -> {
            if (value.get() > curMax) {
                //加入黑名单
                Map<String, Integer> top=getTopThree(IP_QUEUE_CACHE.asMap().keySet());
                for(Map.Entry<String,Integer> entry:top.entrySet()){
                    addBlack(entry.getKey(),"超过指定次数"+window+",max="+max+",cur="+entry.getValue());
                }
            }
        };
        Cache<String, AtomicLong>  cache= Caffeine.newBuilder()
                .expireAfterWrite(window, TimeUnit.MILLISECONDS)
                .maximumSize(MAX_CACHE_IP_SIZE).removalListener(removalListener)
                .build();
        map.put("cache",cache);
        global.add(map);
    }

    public static void init(){
        if(!INITED.getAndSet(true) ){
            IP_QUEUE_CACHE= Caffeine.newBuilder()
                    .expireAfterWrite(5, TimeUnit.MINUTES)
                    .maximumSize(2500)
                    .build();

            BLACK_MAP= Caffeine.newBuilder()
                    .expireAfterWrite(1, TimeUnit.HOURS)
                    .maximumSize(2500)
                    .build();
            initFilterList();
            CompletableFuture.supplyAsync(()->{
                try {
                    Map<String,String> request   =null;
                    do{
                        request = IP_VISIT_QUEUE.takeFirst() ;
                        LOGGER.info("接收到消息--{}",request);
                        ipSafe(request);
                    }while(request!=null&&!request.isEmpty());
                }catch (Exception e){
                    LOGGER.error("消费消息异常", e);
                }
                return "";
            });
        }
    }
    private static  AtomicLong index=new AtomicLong(0);
    private  static  void ipSafe(Map<String,String> request){
        String ip=request.get("ip");
        String param=request.get("param");

        try {
            if(xss(param)||sql(param)){
                addBlack(ip,"攻击代码");
                return;
            }
            //每个ip排队计算
              IP_QUEUE_CACHE.get(ip+"_"+index.getAndIncrement(),k->{
                return 1;
             });
            //纯IP
            //  ips[index.getAndIncrement() % 1000] = dto.getIp();
            for (int i = 0; i < list.size(); i++) {
                Map<String,Object> filter=list.get(i);
                AtomicLong count=  ((Cache<String, AtomicLong> )filter.get("cache")).get( ip, k -> {
                    return new AtomicLong(0);
                });
                long maxcount=(Long)filter.get("max");
                if(count.incrementAndGet()>maxcount){
                    addBlack(ip,"超过指定次数"+filter.get("window")+",max="+maxcount+",cur="+count.get());
                }
            }
            Map<String,Object> filter=global.get(0);
            AtomicLong count=  ((Cache<String, AtomicLong> )filter.get("cache")).get( ip, k -> {
                return new AtomicLong(0);
            });
            long maxcount=(Long)filter.get("max");
            if(count.incrementAndGet()>maxcount){
                Map<String, Integer> top=getTopThree(IP_QUEUE_CACHE.asMap().keySet());
                for(Map.Entry<String,Integer> entry:top.entrySet()){
                    addBlack(entry.getKey(),"超过指定次数"+window+",max="+max+",cur="+entry.getValue());
                }
            }
        }catch (Exception e){
            LOGGER.error(ip,e);
        }
    }
    private static void addBlack(String ip,String message){

        LOGGER.info("触发黑名单 IP {} {}",ip,message);
        AtomicInteger c= BLACK_MAP.get(ip,k->{
            return new AtomicInteger(0);
        });
        c.incrementAndGet();
    }
    private static  Map<String, Integer> getTopThree(Set<String> set){
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
                    LOGGER.error(ip,e);
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


    private static String badStrReg = "\\b(and|or)\\b.{1,6}?(=|>|<|\\bin\\b|\\blike\\b)|\\/\\*.+?\\*\\/|<\\s*script\\b|\\bEXEC\\b|UNION.+?SELECT|UPDATE.+?SET|INSERT\\s+INTO.+?VALUES|(SELECT|DELETE).+?FROM|(CREATE|ALTER|DROP|TRUNCATE)\\s+(TABLE|DATABASE)";

    private static Pattern sqlPattern = Pattern.compile(badStrReg, Pattern.CASE_INSENSITIVE);//整体都忽略大小写


    private static boolean sql(String value)   {
        try {
            String lowerValue = URLDecoder.decode(value, "UTF-8").toLowerCase();
            return Stream.of(lowerValue.split("\\&"))
                    .map(kp -> kp.substring(kp.indexOf("=") + 1))
                    .parallel()
                    .anyMatch(param -> {
                        if (sqlPattern.matcher(param).find()) {
                            return true;
                        }
                        return false;
                    });
        }catch (Exception e){
            LOGGER.error(value,e);
            return true;}
    }
    private static final Pattern[] PATTERNS = {
            // Avoid anything in a <script> type of expression
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            // Avoid anything in a src='...' type of expression
            Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Remove any lonesome </script> tag
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            // Avoid anything in a <iframe> type of expression
            Pattern.compile("<iframe>(.*?)</iframe>", Pattern.CASE_INSENSITIVE),
            // Remove any lonesome <script ...> tag
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Remove any lonesome <img ...> tag
            Pattern.compile("<img(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Avoid eval(...) expressions
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Avoid expression(...) expressions
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Avoid javascript:... expressions
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            // Avoid vbscript:... expressions
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
            // Avoid onload= expressions
            Pattern.compile("on(load|error|mouseover|submit|reset|focus|click)(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };

    public static Boolean xss(String value) {
        if (Strings.isEmpty(value)) {
            return false;
        }
        for (Pattern scriptPattern : PATTERNS) {
            if (scriptPattern.matcher(value).find()) {
                return true;
            }
        }
        return false;
    }
}
