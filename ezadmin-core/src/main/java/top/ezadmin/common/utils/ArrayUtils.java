package top.ezadmin.common.utils;

 import java.util.*;


public class ArrayUtils {

    public static boolean isEmpty(Object[] cookies) {
        return org.apache.commons.lang.ArrayUtils.isEmpty(cookies);
    }

    public static Object[] toArray(List<Object> paramsStatic) {
        if (paramsStatic == null) {
            return null;
        }
        return paramsStatic.toArray();
    }

    public static boolean contains(String[] vs, Object k) {
            return org.apache.commons.lang.ArrayUtils.contains(vs,k);
    }
}
