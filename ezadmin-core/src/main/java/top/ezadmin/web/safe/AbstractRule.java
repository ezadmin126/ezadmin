package top.ezadmin.web.safe;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class AbstractRule implements Rule {
    private static final Logger logger = LoggerFactory.getLogger(IpPoolRule.class);


    private  static   String reg="/(blog).*";
    private  static Pattern pattern=Pattern.compile(reg);

    public void clear(String ip){

    }
    public String print(){
        return "";
    }

    public String getPatternGroupUrl(String url){
        try {
            if(StringUtils.isBlank(reg)){
                return "";
            }
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()&&matcher.groupCount()==3) {
                String g1 = matcher.group(1);
                String g2 = matcher.group(2);
                String g3 = matcher.group(3);
                return g1.replace(g2, "") + g3;
            }
        }catch (Exception e){
           // logger.error(url,e);
        }
        return "";
    }
    public Map<String,Integer> getIpGroup(Set<String> set){
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
        return groupedNamesCount.entrySet().stream()
                .filter(entry -> entry.getValue() > 3)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) // 按照计数从大到小排序
                .limit(10) // 限制结果集大小为前10个
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }
}
