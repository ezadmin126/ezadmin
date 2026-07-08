package top.ezadmin.common.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public abstract class BeanTools {
    private static Map<String, Object> c = new ConcurrentHashMap<>();
    private static Map<String, Class> d = new ConcurrentHashMap<>();
    private static Object lock = new Object();
    private static final Object lock_class = new Object();

    public static Object applicationInstance(String className)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        if (!c.containsKey(className)) {
            synchronized (lock) {
                if (!c.containsKey(className)) {
                    c.put(className, applicationClass(className).getDeclaredConstructor().newInstance());
                }
            }
        }
        return c.get(className);
    }

    public static boolean contains(String className)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return c.containsKey(className);
    }

    public static Class applicationClass(String className) throws ClassNotFoundException {

        if (!d.containsKey(className)) {
            synchronized (lock_class) {
                if (!d.containsKey(className)) {
                    Class cc = Class.forName(className);
                    d.put(className, cc);
                }
            }
        }
        return d.get(className);
    }
}
