package top.ezadmin.common.utils;

public class NumberUtils {
    public static Object createNumber(String s) {
        return org.apache.commons.lang.math.NumberUtils.createNumber(s);
    }

    public static Long toLong(String toString) {
        return org.apache.commons.lang.math.NumberUtils.toLong(toString);
    }

    public static Long toLong(String toString, long defaultV) {
        return org.apache.commons.lang.math.NumberUtils.toLong(toString, defaultV);
    }

    public static Integer toInt(String field_type) {
        return org.apache.commons.lang.math.NumberUtils.toInt(field_type);
    }

    public static Integer toInt(String field_type, int defaultV) {
        return org.apache.commons.lang.math.NumberUtils.toInt(field_type, defaultV);
    }
    public static boolean isNumber(String field_type ) {
        return org.apache.commons.lang.math.NumberUtils.isNumber(field_type );
    }


}
