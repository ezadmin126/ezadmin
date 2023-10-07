package com.ezadmin.common.utils;

import javax.servlet.http.Cookie;
import java.util.*;


public class ArrayUtils {
    public static boolean isEmpty(Cookie[] cookies) {

        return org.apache.commons.lang.ArrayUtils.isEmpty(cookies);
    }

    public static Object[] toArray(List<Object> paramsStatic) {
        if (paramsStatic == null) {
            return null;
        }
        return paramsStatic.toArray();
    }

}
