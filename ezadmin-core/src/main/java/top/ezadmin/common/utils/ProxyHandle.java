/*
 * Copyright 2017 Focus Technology, Co., Ltd. All rights reserved.
 */
package top.ezadmin.common.utils;


import top.ezadmin.EzClientBootstrap;
import top.ezadmin.common.annotation.EzCacheAnnotation;
import top.ezadmin.plugins.cache.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * ProxyHandle.java
 *

 */
public class ProxyHandle implements InvocationHandler {
    private static Logger logger = LoggerFactory.getLogger(ProxyHandle.class);


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        //logger.info("start invoke method:"+key);
        Object instance = getImplInstanceByMethod(method);

        //processEzConfig(instance);

        Method methodImpl = instance.getClass().getMethod(method.getName(), method.getParameterTypes());
        return invokeMethod(instance, methodImpl, args);
    }



    private Object getImplInstanceByMethod(Method method) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException {
        String clazzName = method.getDeclaringClass().getName().replace(method.getDeclaringClass().getSimpleName(),
                "impl." + method.getDeclaringClass().getSimpleName()) + "Impl";
        Object instance = BeanTools.applicationInstance(clazzName);
        return instance;
    }

    private Object invokeMethod(final Object instance, final Method method,final  Object[] args) throws InvocationTargetException, IllegalAccessException {

        Annotation annotation = method.getAnnotation(EzCacheAnnotation.class);
        if (annotation != null) {
            String key = method.getDeclaringClass().getName() + "_" + method.getName();
            Callback callback = new Callback() {
                @Override
                public Object call(String key) {

                    try {
                        Object obj= method.invoke(instance, args);
                        if(obj==null) {
                            if (method.getReturnType().equals(List.class)) {
                                return Collections.emptyList();
                            } else if (method.getReturnType().equals(Map.class)) {
                                return Collections.emptyMap();
                            }
                        }
                        return obj;
                    } catch (Exception e) {
                        logger.warn("proxy error "+key + "::" + StringUtils.join(args), e);
                        if( method.getReturnType().equals(List.class)){
                            return Collections.emptyList();
                        }else if( method.getReturnType().equals(Map.class)){
                            return Collections.emptyMap();
                        }
                        return "";
                    }
                }
            };
            return EzClientBootstrap.instance().getCache().get(key, args == null ? "" : StringUtils.join(args), callback);
        }

        return method.invoke(instance, args);

    }


}
