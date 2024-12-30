package top.ezadmin.utils;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class NoDirect {

    private static AtomicLong atomicLong=new AtomicLong(1);

    private static String MACHCODE="";//机器码

    private static String orderNo(String start)   {
        int year= Calendar.getInstance().get(Calendar.YEAR);
        SimpleDateFormat format0=new SimpleDateFormat("MMddHHmm");
        String  time=format0.format(new Date());
        String end= StringUtils.leftPad((atomicLong.incrementAndGet()%10000)+"",3,"0");
        return start+MACHCODE+(year%100)+time+end;
    }
    public static String saleorderNo(){
        return orderNo("S");
    }
    public static String capitalNo(){
        return orderNo("ZJ");
    }
    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 200; i++) {
            System.out.println(i+"\t"+saleorderNo());;
            System.out.println(i+"\t"+buyorderNo());;
        }
    }

    public static String buyorderNo() {
        return orderNo("BY");
    }
    public static String bhuyorderNo() {
        return orderNo("BH");
    }
    public static String CH() {
        return orderNo("CH");
    }
    public static String buyorderNoAfter() {
        return orderNo("TB");
    }

    public static String inboundNo() {
        return orderNo("RK");
    }
    public static String snNo() {
        return orderNo("SN");
    }
    public static String outboundNo() {
        return orderNo("CK");
    }

    public static String afterNo() {
        return orderNo("TH");
    }
}
