package top.ezadmin.common.utils;





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
//                if(field.getGenericType() .getTypeName().equalsIgnoreCase("java.lang.Integer")){
//                    field.set(obj,NumberUtils.toInt(map2.get(lowFieldName)));
//                }else{
                    field.set(obj,map2.get(lowFieldName));
               // }
            }
        }
        return obj;
    }
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

}
