package top.ezadmin.common.utils;


import top.ezadmin.common.enums.DefaultParamEnum;
import top.ezadmin.common.enums.ColTypeEnum;
import top.ezadmin.common.enums.JdbcTypeEnum;
import top.ezadmin.common.enums.OperatorEnum;

import top.ezadmin.web.JsonArrayResult;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import java.math.BigDecimal;

import java.util.*;

public class Utils {

    private static long IO_BUFFER_SIZE=4096;

    static Logger logger = LoggerFactory.getLogger(Utils.class);
    private static String UPLOAD_URL = "http://localhost:8888/core/upload.html";

    public static void setUploadHost(String url) {
        UPLOAD_URL = url;
    }



    private static ThreadLocal<List<String>> logThreadList = new ThreadLocal<List<String>>();
    private static ThreadLocal<Long> logTimeThreadList = new ThreadLocal<Long>();
    private static ThreadLocal<String> REQUEST_URL = new ThreadLocal<String>();
    private static ThreadLocal<Object> qlParam = new ThreadLocal<Object>();

    /**
     * 外部系统初始化
     */
    public static void initRequestUrl(String url){
        REQUEST_URL.set(url);
    }
    public static String getRequestUrl(){
        return REQUEST_URL.get();
    }


    private static void add(String s){

        if (logThreadList.get() == null) return;
        if(logTimeThreadList.get()==null){
            logTimeThreadList.set(System.currentTimeMillis());
        }
        logThreadList.get().add("time:"+(System.currentTimeMillis()-logTimeThreadList.get())+"ms \t\n"+s);
        logTimeThreadList.set(System.currentTimeMillis());
        logger.info(s);
    }
    /**
     * 把当前日志放到 尾部
     */
    public static void addLog(String s,Object obj) {
        add(s+"\t"+JSONUtils.toJSONString(obj));
    }
    public static void addLog(String s ) {
        add(EzDateUtils.todayDatetime()+ "\t\n" +s );
    }
    public static void addLog(String s,Exception e ) {
        add(EzDateUtils.todayDatetime()+ "\t\n" +s  + ExceptionUtils.getFullStackTrace(e));
        logger.error(s,e);
    }


    public static List<String> getLog() {
        return logThreadList.get();
    }

    public static void initLog() {
        if (logThreadList.get() == null) logThreadList.set(new ArrayList<String>());
    }

    public static void clearLog() {
        logThreadList.remove();
        logTimeThreadList.remove();
        REQUEST_URL.remove();
        qlParam.remove();
    }
    public static void addParam(Object obj){
        qlParam.set(obj);
    }
    public static Object getParam( ){
       return qlParam.get();
    }


    public static List<String> jdbcTypeList = new ArrayList<>();

    static {
        for (int i = 0; i < JdbcTypeEnum.values().length; i++) {
            jdbcTypeList.add(JdbcTypeEnum.values()[i].name());
        }
    }

    public static List<String> firstColList = new ArrayList<>();

    static {
        for (int i = 0; i < ColTypeEnum.values().length; i++) {
            firstColList.add(ColTypeEnum.values()[i].name());
        }
    }

    public static List<Map<String, String>> OperatorList = new ArrayList<>();

    static {
        for (int i = 0; i < OperatorEnum.values().length; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("VALUE", OperatorEnum.values()[i].name());
            map.put("TITLE", OperatorEnum.values()[i].getNameC());
            OperatorList.add(map);
        }
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return coll != null && !coll.isEmpty();
    }
    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isNotEmpty(Map coll) {
        return coll != null && !coll.isEmpty();
    }
    public static boolean isEmpty(Map coll) {
        return coll == null ||coll.isEmpty();
    }

    public static boolean isTimeField(String field) {
        return StringUtils.endsWithIgnoreCase(field, "time");
    }

    public static String getStringByObject(Map<String,Object> p,String key){
        if(Utils.isNotEmpty(p)){
            return trimNull(p.get(key));
        }
        return "";
    }
    public static String trimNull(Object s) {
        return s == null ? "" : s.toString().trim();
    }
    public static String trimNullDefault(Object s,String... def) {
        if(s!=null){
            return trimNull(s);
        }
        for (int i = 0; i < def.length; i++) {
            if(StringUtils.isNotBlank(def[i]) ){
                return def[i];
            }
        }
        return StringUtils.EMPTY;
    }
    public static String trimEmptyDefault(Object s,String... def) {
        if(StringUtils.isNotBlank(trimNull(s))){
            return trimNull(s);
        }
        for (int i = 0; i < def.length; i++) {
            if(StringUtils.isNotBlank(def[i]) ){
                return def[i];
            }
        }
        return StringUtils.EMPTY;
    }
    public static String trimNullJson(Object s) {
        if(s==null){
            return "";
        }
        if(s instanceof  String){
            return s.toString().trim();
        }
        return JSONUtils.toJSONString(s);
    }
//    public static String postBody2WithTimeOut(String url, String json) throws IOException {
//        return "";
//    }

        public static String postBody2WithTimeOut(String urlx, String json) throws IOException {
            return  HttpURLConnectionGetAndPost.doPost(urlx,json,3000);

        }
    public static String postBody2WithTimeOut(String urlx, String json,int millsecond) throws IOException {
         return HttpURLConnectionGetAndPost.doPost(urlx,json,millsecond);
    }



    public static String postBody2WithTimeOut3s(String url, String json) throws IOException {
      return  HttpURLConnectionGetAndPost.doPost(url,json,3000);
    }
    public static Map<String, Map<String, Object>> listToMap(List<Map<String, Object>> list, String fieldName) {
        Map<String, Map<String, Object>> map = new HashMap<>();
        if (Utils.isNotEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> item=list.get(i);
                if (item.containsKey(fieldName)) {
                    map.put(trimNull(item.get(fieldName)), item);
                }
            }
        }
        return map;
    }

    public static String loadHttp(String id,String initData) throws Exception {
        if(Utils.getLog()!=null) {
            Utils.addLog("开始远程请求"+id+initData);
        }
        Map<String, List<String>> paramMap = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add(id);
        Map<String, String> requestParam = JSONUtils.parseMap(initData);
        paramMap.put(requestParam.get("requestName"), list);
        String jsonResult=Utils.postBody2WithTimeOut3s(
                requestParam.get("url"), JSONUtils.toJSONString(paramMap));

        if(Utils.getLog()!=null) {
            Utils.addLog("开始远程请求"+id+ "返回数据："+jsonResult);
        }

        JsonArrayResult test = JSONUtils.parseObject(jsonResult, JsonArrayResult.class);

        String responseKey=requestParam.get("responseKeys");
        if(StringUtils.isNotBlank(responseKey)){
            String key[]=responseKey.split(",");
            StringBuilder finalResult=new StringBuilder();
            for (int j = 0; j < key.length; j++) {
                Object v=test.getData().get(0).get(key[j]);
                v=v==null?"0":v;
                finalResult.append("/"+v);
            }
            if(finalResult.length()>1){
                String result=finalResult.substring(1);
                if(Utils.getLog()!=null) {
                    Utils.addLog("开始远程请求"+id+ "返回数据："+result);
                }
                return result  ;
            }else{
                if(Utils.getLog()!=null) {
                    Utils.addLog("开始远程请求"+id+ "finalResult <1 返回数据：defaultValue");
                }
              return requestParam.get("defaultValue");
            }
        }else{
            if(Utils.getLog()!=null) {
                Utils.addLog("开始远程请求"+id+ "  返回数据：defaultValue ,reason responseKey is null");
            }
            return requestParam.get("defaultValue");
        }
    }

    @Deprecated
    public static Map<String,String> transRequestMap(Map<String, String[]> reqeustMap){
        Map<String,String> searchParamsValues=new HashMap<>();
        for (Map.Entry<String,String[]> entry:reqeustMap.entrySet()){
            String[] v=entry.getValue();
            String k=entry.getKey();
            if(v==null){
               continue;
            }
            if(v!=null&&v.length==1){
                searchParamsValues.put(k, v[0]);

                if(v[0].equalsIgnoreCase(DefaultParamEnum.EZ_TODAY_DATE.name())){
                    searchParamsValues.put(k,DefaultParamEnum.getValue(DefaultParamEnum.EZ_TODAY_DATE.name()));
                }
            }else if(v.length>1){
                searchParamsValues.put(k, Utils.trimNull(StringUtils.join(v,",")));
            }
        }
        return searchParamsValues;
    }

    public static String textareaToXhtml(String str){
        if(StringUtils.isBlank(str)){
            return "";
        }
        StringBuilder sb=new StringBuilder();
        char[] cc=str.toCharArray();
        for (int i = 0; i < cc.length; i++) {
            switch (cc[i]){
                case '\'':sb.append("&apos;"); break;
                case '\"':sb.append("&quot;"); break;
                case '<':sb.append("&lt;"); break;
                case '>':sb.append("&gt;"); break;
                case '(':sb.append("&#40;"); break;
                case '&':sb.append("&amp;"); break;
                case ')':sb.append("&#41;"); break;
                case '\n':sb.append("<br>"); break;
                case '\r':sb.append("<br>"); break;
                case '\t':sb.append(" "); break;
                default:sb.append(cc[i]);
            }
        }
        return sb.toString();
    }
    /**
     * Copy all data from the reader to the writer and close the reader.
     * Exceptions while closing are ignored.
     *
     * @param in the reader
     * @param out the writer (null if writing is not required)
     * @param length the maximum number of bytes to copy
     * @return the number of characters copied
     * @throws IOException on failure
     */
    public static long copyAndCloseInput(Reader in, Writer out, long length)
            throws IOException {
        try {
            long copied = 0;
            int len = (int) Math.min(length,  IO_BUFFER_SIZE);
            char[] buffer = new char[len];
            while (length > 0) {
                len = in.read(buffer, 0, len);
                if (len < 0) {
                    break;
                }
                if (out != null) {
                    out.write(buffer, 0, len);
                }
                copied += len;
                length -= len;
                len = (int) Math.min(length, IO_BUFFER_SIZE);
            }
            return copied;
        } catch (Exception e) {
            throw  e;
        } finally {
            in.close();
        }
    }
    public static List<Map<String,Object>> flatTree(List<Map<String,Object>> list){
        Map  root=new HashMap();
        root.put("ID","0");
        flatTree2(root,list);
        return (List<Map<String,Object>>)root.get("CHILDREN");
    }
    static  void flatTree2( Map<String,Object>  root,List<Map<String,Object>> left){
        for (int i = 0; i < left.size(); i++) {
            Map cur=left.get(i);
            if(StringUtils.equals(Utils.trimNull(cur.get("ID")),"0")){
                continue;
            }
            if( StringUtils.equals(Utils.trimNull(cur.get("PARENT_ID")),Utils.trimNull(root.get("ID")))){
                if(!root .containsKey("CHILDREN")){
                    root.put("CHILDREN",new ArrayList<>());
                }
                ArrayList<Map<String,Object>> cc=( ArrayList<Map<String,Object>>)root.get("CHILDREN");
                cc.add(cur);
                flatTree2(cur,left);
            }
        }
    }

    public static BigDecimal toTax( BigDecimal price){
        // 定义税率常量
        final BigDecimal taxRate = new BigDecimal("0.13");
        // 计算税额和售价
        BigDecimal tax = price.multiply(taxRate);
        BigDecimal totalPrice = price.add(tax);
        return totalPrice.setScale(2,BigDecimal.ROUND_HALF_UP);
    }

    public static void putIfAbsent(Map<String, String> source,String key,String value){
            if(!source.containsKey(key)||StringUtils.isBlank(source.get(key))){
                source.put(key,value);
            }
    }
    public static void putIfAbsent(Map<String, Object> source,String key,Object value){
        if(!source.containsKey(key)||StringUtils.isBlank(Utils.trimNull(source.get(key)))){
            source.put(key,value);
        }
    }
    public static void putIfAbsentExclude0(Map<String, Object> source,String key,Object value){
        if(!source.containsKey(key)&&NumberUtils.toInt(value+"")!=0){
            source.put(key,value);
        }
    }


}
