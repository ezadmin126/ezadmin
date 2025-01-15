package top.ezadmin.common.utils;

import org.apache.commons.beanutils.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
     * 自定义 DateConverter
     */
public class DateConverter implements Converter {

    private final SimpleDateFormat[] dateFormats = {
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
            new SimpleDateFormat("yyyy-MM-dd"),
    };
    @Override
    public Object convert(Class aClass, Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return   value;
        }
        if (value instanceof String) {
            String dateStr = (String) value;
            for (SimpleDateFormat dateFormat : dateFormats) {
                try {
                    return  dateFormat.parse(dateStr);
                } catch (ParseException e) {
                    // 继续尝试下一个格式
                }
            }
        }
        return null;
    }
}