package top.ezadmin.common.utils;


import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public abstract class BeanTools {
    private static Map<String, Object> c = new ConcurrentHashMap<>();
    private static Map<String, Class> d = new ConcurrentHashMap<>();
    private static Object lock = new Object();
    private static final Object lock_class = new Object();

    public static Object applicationInstance(String className)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        if (!c.containsKey(className)) {
            synchronized (lock) {
                if (!c.containsKey(className)) {
                    c.put(className, applicationClass(className).newInstance());
                }
            }
        }
        return c.get(className);
    }

    public static boolean contains(String className)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
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
@Deprecated
    public static Object mapToBean(Map<String,String> map, Class  className) throws IllegalAccessException, InstantiationException {
        Object obj=className.newInstance();
        Map<String,String> map2=new HashMap<String,String>();
        for (Map.Entry<String,String> enty:map.entrySet())
        {
            map2.put(enty.getKey().replace("_","").toLowerCase(Locale.ROOT),enty.getValue());
        } ;
        List<Field> list=getAllFieldsList(className);
        for (int i = 0; i <list.size(); i++) {
            Field field=  list.get(i);
            String lowFieldName=field.getName().toLowerCase(Locale.ROOT);
            if(map2.containsKey(lowFieldName)){
                field.setAccessible(true);
                field.set(obj,map2.get(lowFieldName));
            }
        }
        return obj;
    }
   @Deprecated
    public static List<Field> getAllFieldsList(final Class<?> cls) {
        final List<Field> allFields = new ArrayList<Field>();
        Class<?> currentClass = cls;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            for (final Field field : declaredFields) {
                allFields.add(field);
            }
            currentClass = currentClass.getSuperclass();
        }
        return allFields;
    }

    static {
        // 注册自定义的 DateConverter
        ConvertUtils.register(new DateConverter(), Date.class);
    }
    /**
     * 将 Map 转换为 Java 对象
     * map的字段是大写的数据库的字段 java是驼峰
     *
     * @param map  包含数据的 Map
     * @param clazz 目标对象的 Class 类型
     * @param <T>   目标对象的类型
     * @return 转换后的对象
     * @throws Exception 如果转换过程中发生异常
     */
    public static <T> T convertToEntity(Map<String, Object> map, Class<T> clazz) throws Exception {
        // 创建目标对象实例
        T entity = clazz.getDeclaredConstructor().newInstance();

        // 将 Map 的 key 转换为驼峰命名
        Map<String, Object> camelCaseMap = convertKeysToCamelCase(map);

        // 使用 BeanUtils 将 Map 的值填充到对象中
        BeanUtils.populate(entity, camelCaseMap);

        return entity;
    }


    /**
     * 将 Map 的 key 从大写转换为驼峰命名
     *
     * @param map 原始 Map
     * @return 转换后的 Map
     */
    private static Map<String, Object> convertKeysToCamelCase(Map<String, Object> map) {
        Map<String, Object> camelCaseMap = new java.util.HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String camelCaseKey = convertToCamelCase(entry.getKey());
            camelCaseMap.put(camelCaseKey, entry.getValue());
        }
        return camelCaseMap;
    }

    /**
     * 将大写字段名转换为驼峰字段名
     *
     * @param fieldName 大写字段名（例如：TRADER_TIME）
     * @return 驼峰字段名（例如：traderTime）
     */
    private static String convertToCamelCase(String fieldName) {
        if (fieldName == null || fieldName.isEmpty()) {
            return fieldName;
        }
        String[] parts = fieldName.split("_");
        StringBuilder camelCaseName = new StringBuilder(parts[0].toLowerCase());
        for (int i = 1; i < parts.length; i++) {
            camelCaseName.append(parts[i].substring(0, 1).toUpperCase())
                    .append(parts[i].substring(1).toLowerCase());
        }
        return camelCaseName.toString();
    }


}
