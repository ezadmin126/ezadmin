package top.ezadmin.plugins.parser;

import top.ezadmin.plugins.parser.parse.GenericTokenParser;
import top.ezadmin.plugins.parser.parse.Params;
import top.ezadmin.plugins.parser.parse.ResultModel;
import top.ezadmin.plugins.parser.parse.TokenHandler;
import top.ezadmin.dao.ClobParam;
import top.ezadmin.common.enums.JdbcTypeEnum;
import top.ezadmin.common.utils.NumberUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.common.utils.StringUtils;


import java.util.HashMap;
import java.util.Map;

/**
 * jdbcType=NUMBER 默认为0  如果没有参数，则原样返回
 */
public class StandardSqlParser {
    private StandardSqlParser() {
        // Prevent Instantiation
    }

    private static String startFixStr =  "#{";
    private static String endFixStr =  "}";

    public static ResultModel parse(String string, Map<String,Object> variables) {
        ResultModel model2=new ResultModel();
        if(variables==null|| variables.isEmpty()){
            model2.setResult(string);
            return model2;
        }
        if(string.indexOf(startFixStr)>=0){
            ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler(model2,variables);
            GenericTokenParser parser = new GenericTokenParser(startFixStr, endFixStr, handler);
            model2.setResult(parser.parse(string));
        }else{
            model2.setResult( string );
        }
        return model2;
    }
    public static ResultModel parse(String string) {
        ResultModel model2=new ResultModel();

        if(string.indexOf(startFixStr)>=0){
            ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler(model2, new HashMap<String, Object>());
            GenericTokenParser parser = new GenericTokenParser(startFixStr, endFixStr, handler);
            model2.setResult(parser.parse(string));
        }else{
            model2.setResult( string );
        }
        return model2;
    }
    public static String getKey(String content){
        int s=StringUtils.strip(content).indexOf(startFixStr);
        if(s>=0){

            int last=content.indexOf(",");
            if(last<=0){
                last=content.indexOf(endFixStr);
            }
            return content.substring(s+startFixStr.length(),last);
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println(getKey("#{u,jdbc=a,v=ss}"));
        System.out.println(getKey("#{u}"));
        System.out.println(getKey("#{u,jdbc=a,v=ss}"));
    }

    private static class ParameterMappingTokenHandler  implements TokenHandler {

        private final Map<String,Object> variables;
        ResultModel model;

        public ParameterMappingTokenHandler(ResultModel model2,Map<String, Object> additionalParameters) {
            this.variables = additionalParameters;
            this.model=model2;
        }

        /**
         *
         * @param content
         * @return
         */

        @Override
        public String handleToken(String content) {
            //#{user_id,jdbcType=VARCHAR,value='VARCHAR'}
            String keyAndType[]= StringUtils.split(content,",",3);
            if(keyAndType.length==0){
                return content;
            }
            String key=StringUtils.trimEmpty(keyAndType[0]);
            Params p=new Params();
            p.setParamKey(key);
            //解析jdbctype与 value
            for (int i = 1; i <keyAndType.length ; i++) {
                String kvtype=StringUtils.strip(keyAndType[i]);
                if(kvtype.contains("=")){
                    String[] kv=StringUtils.split(kvtype,"=",2);
                    if(Utils.trimNull(kv[0]).equalsIgnoreCase("jdbctype")){
                        p.setJdbcType(kv[1].toUpperCase());
                    }
                    if(Utils.trimNull(kv[0]).equalsIgnoreCase("value")){
                        p.setParamValue(kv[1]);
                    }
                }
            }
            //无传参
            if(!variables.containsKey(key)){
                if(p.getParamValue()==null){
                    return null;
                }else{ //有设置value
                    return  Utils.trimNull(p.getParamValue());
                }
            }
            Object value= transJavaType(variables.get(key),p.getJdbcType());
            if(value==null){
                if(p.getParamValue()==null){
                    return null;
                }else{ //有设置value
                    return  Utils.trimNull(p.getParamValue());
                }
            }
            //传了参数
            p.setParamValue(value);
            model.addParam(p); //jdbc参数用了null
            return "?";
        }
        public static Object transJavaType(Object obj, String type) {
            if(obj==null){
                return null;
            }
            if (obj instanceof ClobParam) {
                return obj;
            }
            if (JdbcTypeEnum.NUMBER.getName().equalsIgnoreCase(type)) {
                if(StringUtils.isBlank(obj+"")){
                    return null;
                }
                return NumberUtils.createNumber( Utils.trimEmptyDefault(obj,"0"));
            } else if (JdbcTypeEnum.CLOB.getName().equalsIgnoreCase(type)) {
                ClobParam clob=new ClobParam();
                clob.setClob(Utils.trimNull(obj));
                return clob;
            }
            return StringUtils.isBlank(Utils.trimNull(obj))? null : String.valueOf(obj);
        }
    }
}
