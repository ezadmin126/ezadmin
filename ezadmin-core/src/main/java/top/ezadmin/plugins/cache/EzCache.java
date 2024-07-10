package top.ezadmin.plugins.cache;

/**
 * : EzCache
 * @author EzAdmin
 */
public interface EzCache {
    //5分钟缓存
    Object get5(String namespace,String id, Callback call);
    //1小时缓存
    Object get60(String namespace,String id, Callback call);
    //永久缓存
    Object get(String namespace,String id, Callback call);

    void clear();
    void clearKeys(String key);

    void cacheFlag(boolean cache);
}
