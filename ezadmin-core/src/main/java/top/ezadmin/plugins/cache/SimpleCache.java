package top.ezadmin.plugins.cache;

import top.ezadmin.common.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * : SimpleCache
 * @author EzAdmin
 * 此缓存 只实现永久缓存，使用ConcurrentHashMap
 */
public class SimpleCache implements EzCache{

    public static final Logger log = LoggerFactory.getLogger(MyCache.class);

    private static Map<String,Object> foreverCache=new ConcurrentHashMap<>();
    private static Map<String,Object> sixTyMinCache=new ConcurrentHashMap<>();
    private static Map<String,Object> fiveMinCache=new ConcurrentHashMap<>();

    Object lock =new Object();
    private boolean cacheFlag=false;
    @Override
    public void cacheFlag(boolean cache) {
        cacheFlag=cache;
    }
    public Object get5(String namespace,String id, Callback call){
        if (namespace == null||id==null) {
            return null;
        }
        if(!cacheFlag){
            return  call.call(id);
        }
        String key1=  namespace+"_"+id;

        if(fiveMinCache.get(key1)!=null){
            return fiveMinCache.get(key1) ;
        }else{
            synchronized (lock){
                try {
                    if (fiveMinCache.get(key1)!=null) {
                        return fiveMinCache.get(key1) ;
                    } else {
                        fiveMinCache.put(key1, call.call(id));
                    }
                }catch (Exception e){
                    if(Utils.getLog()!=null){
                        Utils.getLog().add(ExceptionUtils.getFullStackTrace(e));
                    }
                }
            }
        }
        return fiveMinCache.get(key1);
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

        if(sixTyMinCache.get(key1)!=null){
            if(Utils.getLog()!=null){
                Utils.getLog().add("get60 containsKey"+namespace+id);
            }
            Object obj= sixTyMinCache.get(key1) ;

            if(Utils.getLog()!=null){
                Utils.getLog().add("get60 containsKey"+namespace+id+obj);
            }
            return obj;
        }else{
            synchronized (lock){
                try {
                    if (sixTyMinCache.get(key1)!=null) {
                        return sixTyMinCache.get(key1) ;
                    } else {
                        sixTyMinCache.put(  key1, call.call(id)) ;
                    }
                }catch (Exception e){
                    // log.error("",e);
                    if(Utils.getLog()!=null){
                        Utils.getLog().add(ExceptionUtils.getFullStackTrace(e));
                    }
                }
            }
        }
        return sixTyMinCache.get(key1);
    }

    public Object get(String namespace,String id, Callback call) {
        if (namespace == null||id==null) {
            return null;
        }
        if(!cacheFlag){
            return  call.call(id);
        }
        String key1=  namespace+"_"+id;

        if(foreverCache.get(key1)!=null){
            return foreverCache.get(key1);
        }else{
            synchronized (lock){
                try {
                    if (foreverCache.get(key1)!=null) {
                        return foreverCache.get(key1);
                    } else {
                        Object obj=call.call(id);
                        if(obj!=null){
                            foreverCache.put( key1, obj );
                        }
                    }
                }catch (Exception e){
                    if(Utils.getLog()!=null){
                        Utils.getLog().add(ExceptionUtils.getFullStackTrace(e));
                    }
                }
            }
        }
        return foreverCache.get(key1);
    }



    public void clearKeys(String key) {
        log.info("  缓存刷新成功");
        for (Map.Entry<String,Object> entry:foreverCache.entrySet()){
            if(StringUtils.containsIgnoreCase(entry.getKey(),key)){
                foreverCache.remove(entry.getKey());
            }
        }
    }
    public void clear() {
        log.info("  缓存刷新成功");
        foreverCache.clear();
        fiveMinCache.clear();
        sixTyMinCache.clear();
    }

    public void clearNameKeys(String namespace, String id) {
        String key1=  namespace+"_"+id;
        clearKeys(key1);
    }
}
