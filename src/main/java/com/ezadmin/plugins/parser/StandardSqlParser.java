package com.ezadmin.plugins.parser;

import com.ezadmin.plugins.parser.parse.GenericTokenParser;
import com.ezadmin.plugins.parser.parse.Params;
import com.ezadmin.plugins.parser.parse.ResultModel;
import com.ezadmin.plugins.parser.parse.TokenHandler;
import com.ezadmin.biz.dao.ClobParam;
import com.ezadmin.common.enums.JdbcTypeEnum;
import com.ezadmin.common.utils.NumberUtils;
import com.ezadmin.common.utils.Utils;
import com.ezadmin.common.utils.StringUtils;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    private static class ParameterMappingTokenHandler  implements TokenHandler {

        private final Map<String,Object> variables;
        ResultModel model;

        public ParameterMappingTokenHandler(ResultModel model2,Map<String, Object> additionalParameters) {
            this.variables = additionalParameters;
            this.model=model2;
        }


        @Override
        public String handleToken(String content) {
            //#{user_id,jdbcType=VARCHAR}
            String keyAndType[]=StringUtils.split(content,",");
            String key=StringUtils.trimEmpty(keyAndType[0]);
            Params p=new Params();
            p.setParamKey(key);
            if(keyAndType.length==2){
                if(keyAndType[1].contains("=")){
                    String[] kv=keyAndType[1].split("=");
                    if(Utils.trimNull(kv[0]).equalsIgnoreCase("jdbctype")){

                        p.setJdbcType(kv[1].toUpperCase());
                    }
                    if(Utils.trimNull(kv[0]).equalsIgnoreCase("value")){
                        p.setParamValue(kv[1]);
                    }
                }
            }
            //默认value,如果有传参，就是参数，如果参数有jdbctype，就做一层转换
            if(!variables.containsKey(key)){
                model.addParam(p);
                return startFixStr+content+endFixStr;
            }
            String realv=Utils.trimNull(variables.get(key));
            if(StringUtils.isNotBlank(realv)){
                p.setParamValue(Utils.trimNull(variables.get(key)));
            }
            String realjv=Utils.trimNull(transJavaType(variables.get(key),p.getJdbcType()));
            if(keyAndType.length==2&&StringUtils.isNotBlank(realjv)){
                p.setParamValue(transJavaType(variables.get(key),p.getJdbcType()));
            }
            model.addParam(p);
            return "?";
        }
        public static Object transJavaType(Object obj, String type) {
            if (obj instanceof ClobParam) {
                return obj;
            }
            if (JdbcTypeEnum.NUMBER.getName().equalsIgnoreCase(type)) {
                return NumberUtils.createNumber( Utils.trimEmptyDefault(obj,"0"));
            } else if (JdbcTypeEnum.CLOB.getName().equalsIgnoreCase(type)) {
                ClobParam clob=new ClobParam();
                clob.setClob(Utils.trimNull(obj));
                return clob;
            } else if (JdbcTypeEnum.VARCHAR.getName().equalsIgnoreCase(type)) {
                return obj == null ? "" : String.valueOf(obj);
            }else if (JdbcTypeEnum.DATE.getName().equalsIgnoreCase(type)
        ||JdbcTypeEnum.DATETIME.getName().equalsIgnoreCase(type)) {
                return StringUtils.isBlank(Utils.trimNull(obj))? null : String.valueOf(obj);
            }
            return String.valueOf(obj);
        }



    }
}
