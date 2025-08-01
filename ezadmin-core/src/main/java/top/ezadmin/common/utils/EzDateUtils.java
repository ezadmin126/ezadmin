package top.ezadmin.common.utils;

import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * : EzDateUtils
 * @author EzAdmin
 */
public class EzDateUtils{
    private static String DATETIME="yyyy-MM-dd HH:mm:ss";
    private static String DATE="yyyy-MM-dd";
    private static String MONTH="yyyy-MM";
    private static String DAY_START_SUF=" 00:00:00";
    private static String DAY_END_SUF=" 23:59:59";


    /**
     * 第i天前的日期  00:00:00
     */
    public static String preDay(int i){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simple = new SimpleDateFormat(DATE);
        calendar.add(Calendar.DATE,-i);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        return simple.format(calendar.getTime())+" - "+simple.format(new Date());
    }
    /**
     * 第i天后的日期  23:59:59
     */
    public static String afterDay(int i){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simple = new SimpleDateFormat(DATE);
        calendar.add(Calendar.DATE,i);
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        return simple.format(calendar.getTime());
    }

    public static String yearStart() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simple = new SimpleDateFormat(DATE);
        calendar.set(Calendar.DAY_OF_MONTH,1);
       calendar.set(Calendar.MONTH,0);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        return simple.format(calendar.getTime())+" - "+simple.format(new Date());
    }
    public static String monthStart() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simple = new SimpleDateFormat(DATE);
        calendar.set(Calendar.DAY_OF_MONTH,1);
      //  calendar.add(Calendar.MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        return simple.format(calendar.getTime())+" - "+simple.format(new Date());
    }

    public static String monthEnd() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simple = new SimpleDateFormat(DATE);
        //先找到下个月第一天
        calendar.add(Calendar.MONTH,1);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.add(Calendar.SECOND,-1);
        return simple.format(calendar.getTime())+" - "+simple.format(new Date());
    }

    public static String trimDateStart(String date){

        SimpleDateFormat simple2 = new SimpleDateFormat(DATETIME);
        SimpleDateFormat simple = new SimpleDateFormat(DATE);
        try {
            Date d=simple.parse(date);
            return simple2.format(d) ;
        }catch (ParseException e){
        }
        return date;
    }
    public static String trimDateEnd(String date){

        SimpleDateFormat simple2 = new SimpleDateFormat(DATETIME);
        SimpleDateFormat simple = new SimpleDateFormat(DATE);
        try {
            Date d=simple.parse(date);
            Date d3=DateUtils.addSeconds(d,86400-1 );
            return simple2.format(d3) ;
        }catch (ParseException e){
        }
        return date;
    }
    public static String toDateFormat(String date){
         SimpleDateFormat simple = new SimpleDateFormat(DATE);
        try {
            Date d=simple.parse(date);
            return simple.format(d) ;
        }catch (ParseException e){
        }
        return date;
    }
    public static String toDateTimeFormat(String date){
        SimpleDateFormat simple = new SimpleDateFormat(DATETIME);
        try {
            Date d=simple.parse(date);
            return simple.format(d) ;
        }catch (ParseException e){
        }
        return date;
    }
    public static String toDateTimeFormat(Date date){
        SimpleDateFormat simple = new SimpleDateFormat(DATETIME);
        try {
            return simple.format(date) ;
        }catch (Exception e){
        }
        return "";
    }

    public static String toTimestamp(String date){
        if(StringUtils.isBlank(date)){
            return date;
        }
        SimpleDateFormat simple2 = new SimpleDateFormat(DATETIME);
        SimpleDateFormat simple = new SimpleDateFormat(DATE);
        try {
            Date d=simple2.parse(date);
            return  d.getTime()+"" ;
        }catch (ParseException e){
            try {
            Date d=simple.parse(date);
            return  d.getTime()+"" ;
            }catch (ParseException e2){

            }
        }
        return date;
    }

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat simple = new SimpleDateFormat(DATETIME);
        System.out.println(yearStart());
    }

    public static String todayDate() {
        SimpleDateFormat simple = new SimpleDateFormat(DATE);
        return simple.format(new Date())+" - "+simple.format(new Date());
    }
    public static String todayDateNormal() {
        SimpleDateFormat simple = new SimpleDateFormat(DATE);
        return simple.format(new Date());
    }
    public static String todayDatetime() {
        SimpleDateFormat simple = new SimpleDateFormat(DATETIME);
        return simple.format(new Date());
    }

    public static String currentMonth() {
        SimpleDateFormat simple = new SimpleDateFormat(MONTH);
        return simple.format(new Date());
    }
}
