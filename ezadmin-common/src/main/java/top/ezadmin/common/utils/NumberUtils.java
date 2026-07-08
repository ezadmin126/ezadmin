package top.ezadmin.common.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberUtils {
    public static Object createNumber(String s) {
        if (s == null) {
            return null;
        }
        String value = s.trim();
        if (value.length() == 0) {
            return null;
        }
        char lastChar = value.charAt(value.length() - 1);
        if (lastChar == 'l' || lastChar == 'L') {
            return Long.valueOf(value.substring(0, value.length() - 1));
        }
        if (lastChar == 'f' || lastChar == 'F') {
            return Float.valueOf(value.substring(0, value.length() - 1));
        }
        if (lastChar == 'd' || lastChar == 'D') {
            return Double.valueOf(value.substring(0, value.length() - 1));
        }
        if (value.indexOf('.') >= 0 || value.indexOf('e') >= 0 || value.indexOf('E') >= 0) {
            return new BigDecimal(value);
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException ignored) {
            try {
                return Long.valueOf(value);
            } catch (NumberFormatException ignoredLong) {
                return new BigInteger(value);
            }
        }
    }

    public static Long toLong(String toString) {
        return toLong(toString, 0L);
    }

    public static Long toLong(String toString, long defaultV) {
        if (toString == null) {
            return defaultV;
        }
        try {
            return Long.parseLong(toString.trim());
        } catch (Exception e) {
            return defaultV;
        }
    }

    public static Integer toInt(String field_type) {
        return toInt(field_type, 0);
    }

    public static Integer toInt(String field_type, int defaultV) {
        if (field_type == null) {
            return defaultV;
        }
        try {
            return Integer.parseInt(field_type.trim());
        } catch (Exception e) {
            return defaultV;
        }
    }

    public static boolean isNumber(String field_type) {
        if (field_type == null || field_type.trim().length() == 0) {
            return false;
        }
        try {
            createNumber(field_type);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
