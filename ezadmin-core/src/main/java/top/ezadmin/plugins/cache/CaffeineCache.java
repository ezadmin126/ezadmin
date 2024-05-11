package top.ezadmin.plugins.cache;

import top.ezadmin.common.utils.Utils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class CaffeineCache implements EzCache{

    public static final Logger log = LoggerFactory.getLogger(MyCache.class);
    private static Cache<String, Object> fiveMinCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();
    private static Cache<String, Object> sixTyMinCache = Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .maximumSize(50000)
            .build();
    private static Cache<String, Object> foreverCache = Caffeine.newBuilder()
            .maximumSize(10000)
            .build();


    @Override
    public Object get5(String namespace,String id, Callback call){
        if (namespace == null||id==null) {
            return null;
        }
        if(!cacheFlag){
            return  call.call(id);
        }
        String key1=  namespace+"_"+id;
        return  fiveMinCache.get(key1, k ->  call.call(id));
    }
    public Object get60(String namespace,String id, Callback call){
        if(Utils.getLog()!=null){
            Utils.getLog().add("get60"+namespace+id);
        }
        if (namespace == null||id==null) {
            return "";
        }
        if(!cacheFlag){
            return  call.call(id);
        }
        String key1=  namespace+"_"+id;
        return  sixTyMinCache.get(key1, k ->  call.call(id));
    }

    public Object get(String namespace,String id, Callback call) {
        if (namespace == null||id==null) {
            return null;
        }
        if(!cacheFlag){
            return  call.call(id);
        }
        String key1=  namespace+"_"+id;
        return  foreverCache.get(key1, k ->  call.call(id));
    }
    public void clearKeys(String key) {
        log.info("  缓存刷新成功");
        foreverCache.asMap().forEach((k,v)->{
            if(StringUtils.containsIgnoreCase(k,key)){
                foreverCache.invalidate(k);
            }
        });
    }
    @Override
    public void clear() {

            log.info("  缓存刷新成功");
        foreverCache.invalidateAll();
        fiveMinCache.invalidateAll();
        sixTyMinCache.invalidateAll();

    }

    private boolean cacheFlag=false;
    @Override
    public void cacheFlag(boolean cache) {
        cacheFlag=cache;
    }
}
