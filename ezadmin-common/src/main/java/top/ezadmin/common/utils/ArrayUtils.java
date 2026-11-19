package top.ezadmin.common.utils;

import java.util.List;


public class ArrayUtils {

    public static boolean isEmpty(Object[] cookies) {
        return cookies == null || cookies.length == 0;
    }

    public static Object[] toArray(List<Object> paramsStatic) {
        if (paramsStatic == null) {
            return null;
        }
        return paramsStatic.toArray();
    }

    public static boolean contains(String[] vs, Object k) {
        if (vs == null) {
            return false;
        }
        for (String v : vs) {
            if (v == null ? k == null : v.equals(k)) {
                return true;
            }
        }
        return false;
    }
}
