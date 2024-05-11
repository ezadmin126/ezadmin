package top.ezadmin.common.utils;

import top.ezadmin.plugins.parser.MapParser;
import top.ezadmin.plugins.parser.parse.ResultModel;
import top.ezadmin.common.enums.JdbcTypeEnum;
import top.ezadmin.dao.ClobParam;
import org.apache.commons.lang.BooleanUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtils {
    public static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * null
     */
    static final String JS_PARMA_NULL = "null";

    /**
     * nan
     */
    static final String JS_PARMA_NAN = "nan";

    /**
     * undefined
     */
    static final String JS_PARMA_UNDEFINED = "undefined";


    public static String repaceAll(String repaceStr, String oldStr, String newStr) {
        // 替换字符串
        if (null == repaceStr) {
            return null;
        }
        // 替换字段为null则默认空字符串
        if (null == oldStr) {
            oldStr = "";
        }
        // 新字段为null则默认空字符串
        if (null == newStr) {
            newStr = "";
        }
        int index = repaceStr.indexOf(oldStr);
        // 存在 oldStr
        if (index != -1) {
            repaceStr = repaceStr.substring(0, index) + newStr + repaceStr.substring(index + oldStr.length(), repaceStr.length());
            return repaceAll(repaceStr, oldStr, newStr);
        }
        return repaceStr;
    }


    public static boolean isBlank(String str) {
        if (null == str || str.trim().length() == 0) {
            return true;
        }

        return false;
    }


    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }



    public static boolean isJsBlank(String str) {
        if (isBlank(str)) {
            return true;
        }
        // js 页面 的 null, undefined
        if (JS_PARMA_NULL.equals(str.toLowerCase()) || JS_PARMA_UNDEFINED.equals(str.toLowerCase()) || JS_PARMA_NAN.equals(str.toLowerCase())) {
            return true;
        }

        return false;
    }


    public static boolean isNotJsBlank(String str) {
        return !isJsBlank(str);
    }


    public static boolean match(List<Pattern> list, String s) {
        if (s == null || list == null || list.size() == 0) {
            return false;
        } else {
            for (int i = 0; i < list.size(); i++) {
                Pattern pInclude = list.get(i);
                Matcher matcher = pInclude.matcher(s);
                if (matcher.matches()) {
                    return true;
                }
            }
            return false;
        }
    }


    public static byte[] parseToByte(final InputStream inputStream) throws Exception {
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toByteArray();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String safeDb(String searchFieldValue) {
        if (StringUtils.isBlank(searchFieldValue)) {
            return searchFieldValue;
        }
        String result=StringUtils.replace(searchFieldValue, "'", "''");
        result=StringUtils.replace(result, "\\", "\\\\");
        return result;
    }
    public static String replaceToSearch(String searchFieldValue){
        if (StringUtils.isBlank(searchFieldValue)) {
            return searchFieldValue;
        }
        StringBuilder searchResult=new StringBuilder();
        char[] chars=searchFieldValue.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            switch (chars[i]) {
                case '\'' :
                    searchResult.append("''");
                    break;
                case ',' :
                    appendSingle(searchResult,'%');
                    break;
                case '，' :
                    appendSingle(searchResult,'%');
                    break;
                case '、' :
                    appendSingle(searchResult,'%');
                    break;
                case ' ' ://避免多个空格
                    appendSingle(searchResult,'%');
                case '%' ://避免多个空格
                    appendSingle(searchResult,'%');
                    break;
                default:searchResult.append(chars[i]);
            }
        }
        return searchResult.toString();
    }


    private static void appendSingle(StringBuilder searchResult,char c){
        if(searchResult.length()>0&&searchResult.charAt(searchResult.length()-1)!=c){
            searchResult.append(c);
        }
    }



    public static String EMPTY = "";

    public static String replaceNotPathChar(Object str) {
        if (str == null || StringUtils.isBlank(str + "")) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : str.toString().toCharArray()) {
            if (Character.isLetterOrDigit(c) || IsChinese(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String str = "Hello,   World    Java 语言，GPT-3.5";
        String[] parts = str.split("[,\\s{1,}]");

        for (String part : parts) {
            System.out.println(part);
        }

    }

    public static boolean IsChinese(char c) {
        //通过字节码进行判断
        return c >= 0x4E00 && c <= 0x29FA5;
    }

    public static ResultModel parse(String source, Map<String, Object> replaceMapParam) {
        if (StringUtils.isBlank(source)) {
            return ResultModel.getInstance();
        }
        ResultModel m = MapParser.parse(source, replaceMapParam);

        return m;
    }

    public static ResultModel parseUrl(String source, Map<String, Object> replaceMapParam) {
        if (StringUtils.isBlank(source)) {
            return ResultModel.getInstance();
        }
        ResultModel m = MapParser.parseDefaultEmpty(source, replaceMapParam);

        return m;
    }

    /**
     * @param paramFromRequest
     * @param sqlsplit         ${databa,varchar}  number,date
     * @return
     */
    public static Object transeParamType(Map<String, Object> paramFromRequest, String sqlsplit) {
        String[] ex = explain(sqlsplit);
        Object obj = paramFromRequest.get(ex[0].toUpperCase());
        //logger.info("解析参数 "+sqlsplit +"\ttype:"+ex[1]+"\t"+obj);
        if (obj == null) {
            //logger.warn("无法找到对应参数"+ex[0]);
        }
        return transJavaType(obj, ex[1]);
    }

    //${databa,varchar}
    private static String[] explain(String sqlsplit) {
        if (StringUtils.isBlank(sqlsplit) || sqlsplit.length() < 3 || sqlsplit.charAt(0) != '$') {
            return null;
        }
        sqlsplit = sqlsplit.substring(2, sqlsplit.indexOf("}"));
        String currentParamArray[] = sqlsplit.split(",");
        String type = "jdbcType=VARCHAR";
        if (currentParamArray.length == 2) {
            type = currentParamArray[1].trim();
        }
        return new String[]{currentParamArray[0].toUpperCase(), type};
    }

    public static Object transJavaType(Object obj, String type) {
        if (obj instanceof ClobParam) {
            return obj;
        }
        if (JdbcTypeEnum.NUMBER.getName().equalsIgnoreCase(type)) {
            return NumberUtils.createNumber(obj == null ? "0" : String.valueOf(obj));
        } else if (JdbcTypeEnum.CLOB.getName().equalsIgnoreCase(type)) {
            return obj;
        } else if (JdbcTypeEnum.VARCHAR.getName().equalsIgnoreCase(type)) {
            return type == null ? "" : String.valueOf(obj);
        }
        return String.valueOf(obj);
    }

    public static SqlModel transToSqlModel(String sqlTemplate, Map<String, Object> requestParam) {

        SqlModel model = new SqlModel();
        StringBuilder sqlresult = new StringBuilder();
        String sqlReturnParam = "";//参数
        String sqlBody;//真正执行的sql
        char first = ' ';
        for (char c : sqlTemplate.toCharArray()) {
            if (c == '\r' || c == '\n' || c == ' ') {
                continue;
            }
            first = c;
            break;
        }
        if (first == '$') {
            int eqIndex = sqlTemplate.indexOf("=");
            sqlBody = sqlTemplate.substring(eqIndex + 1);
            sqlReturnParam = sqlTemplate.substring(0, eqIndex);
        } else {
            //没有返回参数
            sqlBody = sqlTemplate;
        }
        // logger.info("解析SQL:参数："+sqlReturnParam+"\t body="+sqlBody);
        int index = sqlBody.indexOf("$");

        if (index < 0) {
            //sqlresult.append(sqlBody );
        } else {
            sqlresult.append(sqlBody.substring(0, index));
        }
        int end = 0;
        while (index > 0) {
            end = sqlBody.indexOf("}", index) + 1;
            String currentParam = sqlBody.substring(index, end);

            Object p = transeParamType(requestParam, currentParam);
            model.getParams().add(p);
            sqlresult.append("?");
            index = sqlBody.indexOf("$", end);
            if (index > 0) {
                String betweenParam = sqlBody.substring(end, index);
                sqlresult.append(betweenParam);
            }
        }
        //解析参数
        String[] ex = explain(sqlReturnParam);

        model.setSql(sqlresult + sqlBody.substring(end));
        if (ex != null && ex.length == 2) {
            model.setReturnParamName(ex[0]);
            model.setReturnParamType(ex[1]);
        }
        // logger.info("SQL解析结果："+model);
        return model;

    }

    public static String replace(String searchFieldValue, String s, String s1) {
        return org.apache.commons.lang.StringUtils.replace(searchFieldValue, s, s1);
    }

    public static boolean equals(String code, String success) {
        return org.apache.commons.lang.StringUtils.equals(code, success);
    }

    public static boolean equalsIgnoreCase(String key, String name) {
        return org.apache.commons.lang.StringUtils.equalsIgnoreCase(key, name);
    }

    public static String upperCase(String field_name) {
        return org.apache.commons.lang.StringUtils.upperCase(field_name);
    }

    public static String join(Object[] value) {
        return org.apache.commons.lang.StringUtils.join(value);
    }

    public static String join(Collection value, String s) {
        return org.apache.commons.lang.StringUtils.join(value, s);
    }

    public static Object join(Object[] value, String s) {
        return org.apache.commons.lang.StringUtils.join(value, s);
    }

    public static boolean contains(String field_name, String time) {
        return org.apache.commons.lang.StringUtils.contains(field_name, time);
    }
    public static int contains(String field_name ) {
        return org.apache.commons.lang.StringUtils.length(field_name );
    }


    public static String[] split(String str, String separatorChars) {
        return org.apache.commons.lang.StringUtils.split(str, separatorChars);
    }
    public static String[] split(String str, String separatorChars,int max) {
        return org.apache.commons.lang.StringUtils.split(str, separatorChars,max);
    }

    public static String[] splitByWholeSeparator(String selectSql, String startFixStr) {
        return org.apache.commons.lang.StringUtils.splitByWholeSeparator(selectSql, startFixStr);
    }

    public static boolean endsWithIgnoreCase(String field, String time) {
        return org.apache.commons.lang.StringUtils.endsWithIgnoreCase(field, time);
    }

    public static String trimEmpty(String s) {

        return org.apache.commons.lang.StringUtils.trimToEmpty(s);
    }

    public static String strip(String s ) {

        return org.apache.commons.lang.StringUtils.strip(s );
    }
    public static String leftPad(Object s,int size,String c ) {

        return org.apache.commons.lang.StringUtils.leftPad(Utils.trimNull(s),size,c );
    }




//    public static boolean startWithTrim(String defaultGroup, String group_) {
//        return trimEmpty(defaultGroup).startsWith(group_);
//    }

    public static boolean startWithTrimAndLower(String defaultGroup, String group_) {
        return org.apache.commons.lang.StringUtils.lowerCase(trimEmpty(defaultGroup)).startsWith(group_);
    }

    public static String lowerCase(String key) {
        return org.apache.commons.lang.StringUtils.lowerCase(key);
    }
    public static boolean toBoolean(String s){
        return BooleanUtils.toBoolean(s);
    }

    public static boolean startsWith(String view, String s) {
        return org.apache.commons.lang.StringUtils.startsWith(view,s);
    }

    public static String filterLetterOrDigit(String ori){
        if(isBlank(ori)){
            return trimEmpty(ori);
        }
        StringBuilder sb=new StringBuilder();
        for(char c:ori.toCharArray()){
            if(Character.isLetterOrDigit(c)){
                sb.append(c);
            }else{
                if(sb.length()>1&&sb.charAt(sb.length()-1)!='-'){
                    sb.append("-");
                }
            }
        }
        return sb.toString();
    }

}
