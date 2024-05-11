/*
 * Copyright 2017 Focus Technology, Co., Ltd. All rights reserved.
 */
package top.ezadmin.common.utils;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EzProxy.java
 *

 */
public class EzProxy {

    private static Map<String, Object> map = new ConcurrentHashMap<>();

    public synchronized static <T> T singleInstance(Class<T> target) {
        if (map.containsKey(target.getName())) {
            return (T) map.get(target.getName());
        }
        T t = (T) Proxy.newProxyInstance(EzProxy.class.getClassLoader(), new Class<?>[]{target}, new ProxyHandle());
        map.put(target.getName(), t);
        return t;
    }
}
