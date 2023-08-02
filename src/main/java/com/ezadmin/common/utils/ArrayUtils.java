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

//    public static List<Long>  mapToListLong(String[] array){
//        if(array==null||array.length==0){
//            return Collections.emptyList();
//        }
//        return Arrays.asList(array).stream().map(item->NumberUtils.toLong(item)).collect(Collectors.toList());
//    }


}
