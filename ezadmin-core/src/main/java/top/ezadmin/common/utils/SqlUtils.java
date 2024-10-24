package top.ezadmin.common.utils;

import top.ezadmin.common.constants.SelectKVContants;
import top.ezadmin.common.enums.JdbcTypeEnum;
import top.ezadmin.common.enums.OperatorEnum;
import top.ezadmin.common.enums.ParamNameEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.dao.model.CustomSearchDTO;
import top.ezadmin.dao.model.CustomSearchGroup;
import top.ezadmin.dao.model.CustomSearchOrder;
import top.ezadmin.dao.model.CustomSearchSingle;

import java.util.*;

import static javax.management.Query.and;


public class SqlUtils {
    static Logger logger = LoggerFactory.getLogger(SqlUtils.class);
    public final static String PREFIX = "#{";
    public final static String SUFIX = "}";
    public final static String _ITEM_VALUE_KEY = "_ITEM_VALUE";
    public final static String _ITEM_VALUE_ORDER_KEY = "_ITEM_VALUE_ORDER";
    public final static String _ITEM_VALUE_START_KEY = "_ITEM_VALUE_START";
    public final static String _ITEM_VALUE_END_KEY = "_ITEM_VALUE_END";

    /**
     *
     */
    public static String transToWhere(String itemOper,String itemName, String itemAliasName
            ,String itemJdbcType,String itemValue,String itemValueStart,String itemValueEnd ) {
        return transToWherePrePare(  itemOper,  itemName,   itemAliasName
                ,  itemJdbcType,  itemValue,  itemValueStart,  itemValueEnd,true," and ");
    }
    public static String transToWherePrePare(String itemOper,String itemName, String itemAliasName
    ,String itemJdbcType,String itemValue,String itemValueStart,String itemValueEnd,boolean prepare,String union ) {
        String result = "";
        String oper = StringUtils.isBlank( itemOper)? OperatorEnum.EQ.getOperC() : itemOper;
        String name = StringUtils.upperCase(itemName);
        String alias=itemAliasName;
        String ITEM_JDBC_TYPE = StringUtils.isBlank(itemJdbcType) ? JdbcTypeEnum.VARCHAR.getName() : itemJdbcType;
        String VALUE = Utils.trimNull(itemValue);
        String VALUE_START = Utils.trimNull(itemValueStart);
        String VALUE_END = Utils.trimNull(itemValueEnd);

        OperatorEnum operatorEnum = OperatorEnum.match(oper);
        switch (operatorEnum) {
            case EQ:
            case NE:
            case LTE:
            case GTE:
                result = normal(union,alias,name, operatorEnum, VALUE,ITEM_JDBC_TYPE,prepare);
                break;
            case BETWEEN:
                result = between(alias,name,   ITEM_JDBC_TYPE, VALUE_START, VALUE_END,union);
                break;
            case LIKE:
            case NOTLIKE:
                result = like(union,alias,name, operatorEnum, VALUE);
                break;
            case IN:
            case NOT_IN:
                result = in(alias,name, operatorEnum, ITEM_JDBC_TYPE, VALUE,union);
                break;
            case ALL_FIND_IN_SET:
                result = find_in_set(alias,name, operatorEnum, ITEM_JDBC_TYPE, VALUE,union);
                break;
            case ALL_EQ:
                result = all(alias,name, operatorEnum, ITEM_JDBC_TYPE, VALUE,union);
                break;
            case ALL_LIKE:
                //todo
            default:
                break;
        }
        return result;
    }

    private static String all(String alias, String name, OperatorEnum operatorEnum, String itemJdbcType, String value, String union) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        value=StringUtils.safeDb(value);
        value=StringUtils.replace(value,"[","");
        value=StringUtils.replace(value,"]","");
        if (StringUtils.isBlank(value)) {
            return "";
        }
        StringBuilder sql = new StringBuilder();
        sql.append(union); //and
        sql.append(" ( 1=1 ");
        String idArray[] = StringUtils.split(value, SelectKVContants.SPLIT.toString()+"|,");
        String fieldName=alias(alias,name);
        if(idArray.length>0){
            for (String item : idArray) {
                if (StringUtils.isBlank(item)) {
                    continue;
                }
                String valueNew= item ;
                if (JdbcTypeEnum.VARCHAR.getName().equals(itemJdbcType)) {
                    sql.append(" and  ").append(fieldName)
                            .append("=  '").append(valueNew).append("'");
                }else{
                    sql.append(" and  ").append(fieldName)
                            .append("=  ").append(valueNew) ;
                }
            }
        }
        sql.append(" ) ");
        return sql.toString();
    }


    private static String in(String alias,String name, OperatorEnum oper, String jdbcType, String value,String union) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        value=StringUtils.safeDb(value);
        value=StringUtils.replace(value,"[","");
        value=StringUtils.replace(value,"]","");
        if (StringUtils.isBlank(value)) {
            return "";
        }
        StringBuilder sql = new StringBuilder();
        sql.append(union);
        sql.append(alias(alias,name));
        sql.append(" "+ oper .getOperC()+" ");
        if (JdbcTypeEnum.VARCHAR.getName().equals(jdbcType)) {
            //K为多个需要分割参与搜索的情况
            String idArray[] = StringUtils.split(value, SelectKVContants.SPLIT.toString()+"|,");
            sql.append("   (");
            StringBuilder sqlPice = new StringBuilder("'-1x'");
            for (String id : idArray) {
                if (StringUtils.isBlank(id)) {
                    continue;
                }
                sqlPice.append(",'");
                sqlPice.append(Utils.trimNull(id));
                sqlPice.append("'");
            }
            sql.append(sqlPice);
            sql.append(" )");
        } else {
            sql.append("  (" + value+ ")");
        }
        return sql.toString();
    }
    private static String find_in_set(String alias,String name, OperatorEnum oper, String jdbcType, String value,String union) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        value=StringUtils.safeDb(value);
        value=StringUtils.replace(value,"[","");
        value=StringUtils.replace(value,"]","");
        if (StringUtils.isBlank(value)) {
            return "";
        }

        StringBuilder sql = new StringBuilder();
        sql.append(union+"   (1=1 ");
        String idArray[] = StringUtils.split(value, SelectKVContants.SPLIT.toString()+"|,");
        if(idArray.length>0){
            for (String id:idArray){
                if (StringUtils.isBlank(id)) {
                    continue;
                }
                sql.append(union+"  find_in_set(");
                if (JdbcTypeEnum.VARCHAR.getName().equals(jdbcType)) {
                    sql.append("'");
                }
                sql.append(id);
                if (JdbcTypeEnum.VARCHAR.getName().equals(jdbcType)) {
                    sql.append("'");
                }
                sql.append(",");
                sql.append(alias(alias,name));
                sql.append(" )");
            }
        }
        sql.append(" )");
        return sql.toString();
    }



    private static String between(String alias,String name, String jdbcType, String valueStart, String valueEnd,String union) {
        StringBuilder sql = new StringBuilder();

        String finalStart = StringUtils.safeDb(valueStart), finalEnd = StringUtils.safeDb(valueEnd);
        if(JdbcTypeEnum.NUMBER.getName().equalsIgnoreCase(jdbcType)){

        }
        if(JdbcTypeEnum.DATE.getName().equalsIgnoreCase(jdbcType)){
            finalStart = " '" + valueStart + "' ";
            finalEnd = " '" + valueEnd + "' ";
        }
        if(JdbcTypeEnum.DATETIME.getName().equalsIgnoreCase(jdbcType)){
            //且不包含时分秒的时候自动补充
            if(!valueStart.contains(":")){
                valueStart = EzDateUtils.trimDateStart(valueStart);
            }
            if(!valueEnd.contains(":")){
                valueEnd = EzDateUtils.trimDateEnd(valueEnd);
            }

            finalStart = " '" + valueStart + "' ";
            finalEnd = " '" + valueEnd + "' ";
        }

        if(JdbcTypeEnum.DATE_TO_NUMBER.getName().equalsIgnoreCase(jdbcType)){
            valueStart=EzDateUtils.trimDateStart(valueStart);
            valueEnd=EzDateUtils.trimDateEnd(valueEnd);
            finalStart=EzDateUtils.toTimestamp(valueStart);
            finalEnd=EzDateUtils.toTimestamp(valueEnd);
        }

        if (StringUtils.isNotBlank(valueStart)) {
            sql.append(union);
            sql.append(alias(alias,name));

            sql.append(" >=  " + finalStart);
        }
        if (StringUtils.isNotBlank(valueEnd)) {
            sql.append(union);
            sql.append(alias(alias,name));
            sql.append(" <=  " + finalEnd);
        }
        sql.append(" ");
        return sql.toString();
    }

    private static String normal(String unionOp,String alias,String name, OperatorEnum oper, String value, String jdbcType,boolean prepare) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(unionOp);
        sb.append(alias(alias,name));
        sb.append(" ");
        sb.append( oper.getOperC());
        sb.append(" ");
        if(prepare){
            sb.append(PREFIX);//#{A,jdbcType=xx}
            sb.append(name);
            if(JdbcTypeEnum.NUMBER.getName().equalsIgnoreCase(jdbcType)){
                sb.append(",jdbcType=NUMBER");
            }
            sb.append(SUFIX);
        }else{
            if(JdbcTypeEnum.NUMBER.getName().equalsIgnoreCase(jdbcType)){
                sb.append(value);
            }else{
                sb.append("'"+value+"'");
            }
        }
        return sb.toString();
    }

    //如果是||分割 则代表或者  如果是 &&分割 代表 且 。
    private static String like(String unionOp,String alias,String name, OperatorEnum oper, String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(unionOp);
        sb.append(alias(alias,name));

        sb.append(" ");
        sb.append( oper.getOperC());
        sb.append(" concat('%','");
      //  sb.append(PREFIX);
        sb.append(StringUtils.replaceToSearch(value));//满足特殊字符分词搜索
//        sb.append(name);
      //  sb.append(SUFIX + ",'%')");
        sb.append(  "','%')");
        return sb.toString();
    }

    public static String like2(String unionOp,String alias,String name, OperatorEnum oper, String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(unionOp);
        sb.append(alias(alias,name));
        sb.append(" ");
        sb.append(oper.getOperC());
        sb.append(" concat('%','");
        sb.append(StringUtils.replaceToSearch(value));
        sb.append(  "','%')");
        return sb.toString();
    }



    public static String alias(String alias,String name){
        if(StringUtils.isBlank(alias)){
            return name;
        }
        if(StringUtils.contains(alias,".")){
            return alias;
        }
        return alias+"."+name;
    }
    /**
     * 普通搜索
     */

    /**
     * 单个单个的搜索
     * @return and (1=2 or name like '%a%'  or  phone like '%a%' )
     */









    public static String buildPageSql(String body,String countExpress,String param,String order,String groupBy,String page,boolean isCount){
        if(isCount){
            return buildCountSql(body,countExpress,param ,groupBy);
        }else{
            return buildSearchSql(body,param,order,groupBy,page);
        }
    }
    public static String buildSearchSql(String body,String param,String order,String groupBy,String page){
        StringBuilder sql=new StringBuilder();
        sql.append(body);
        sql.append(" "  );
        sql.append( param );
        sql.append(" "  );
        if(StringUtils.isNotBlank(groupBy) ){
            sql.append(groupBy );
        }
        if(StringUtils.isNotBlank(order) ){
            sql.append(order );
        }
        sql.append(  page);
        return sql.toString();
    }
    public static String buildCountSql(String body,String countExpress,String where,String groupBy){
        StringBuilder sql=new StringBuilder();
        //包起来的count
        if(StringUtils.isBlank(countExpress )){
            sql.append(" select count(1) from( ");
            sql.append(body );
            sql.append(where);
            if(StringUtils.isNotBlank(groupBy) ){
                sql.append(groupBy );
            }
            sql.append(" ) a" );
        }else // 自动去掉查询条件，然后count
            if(StringUtils.equalsIgnoreCase(countExpress,"count(1)")){
                sql.append("select count(1)   "+body.substring( body.toLowerCase().indexOf("from ")));
                sql.append(where);
                if(StringUtils.isNotBlank(groupBy) ){
                    sql.append(groupBy );
                }
            }else // 纯sql不添加where
                if(body.indexOf("#group")>0){
                    sql.append( body);
                }else if(body.indexOf( "from")<0&&body.indexOf( "FROM")<0){
                    sql.append( body);
                    if(StringUtils.isNotBlank(groupBy) ){
                        sql.append(groupBy );
                    }
                }
                else{ //自定义sql+where
                    sql.append( body);
                    sql.append(where);
                    if(StringUtils.isNotBlank(groupBy) ){
                        sql.append(groupBy );
                    }
                }
        return sql.toString();
    }



    public static String searchToSql(Map<String,Object> search, Map<String,Object> request) {
        StringBuilder result=new StringBuilder();
        String plugin=Utils.getStringByObject(search,JsoupUtil.TYPE);
        String jdbctype=Utils.getStringByObject(search,JsoupUtil.JDBCTYPE);
        String oper=Utils.getStringByObject(search,JsoupUtil.OPER);
        String name=Utils.getStringByObject(search,JsoupUtil.ITEM_NAME);
        String alias=Utils.getStringByObject(search,JsoupUtil.ALIAS);

        //普通字段
        String value=Utils.getStringByObject(search,ParamNameEnum.itemParamValue.getName());

        String valueS=Utils.getStringByObject(search,ParamNameEnum.itemParamValueStart.getName());
        String valueE=Utils.getStringByObject(search,ParamNameEnum.itemParamValueEnd.getName());

        // 多字段搜索
        String itemSearchKey=Utils.getStringByObject(search, ParamNameEnum.itemSearchKey.getName() );
        String itemSearchValue=Utils.getStringByObject(search,ParamNameEnum.itemSearchValue.getName());

        //多字段合并后搜索
        String itemSearchConcatValue=Utils.getStringByObject(search,ParamNameEnum.itemSearchConcatValue.getName());

        //多字段时间搜索
        String itemSearchDateKey=Utils.getStringByObject(search,ParamNameEnum.itemSearchDateKey.getName());
        String itemSearchDateValueStart=Utils.getStringByObject(search,ParamNameEnum.itemSearchDateValueStart.getName());
        String itemSearchDateValueEnd=Utils.getStringByObject(search,ParamNameEnum.itemSearchDateValueEnd.getName());


        //常规
        if(!plugin.equalsIgnoreCase("hidden-nowhere")&&
                !jdbctype.equalsIgnoreCase("BODY")){
            //不拼接where
            result.append( SqlUtils.transToWhere(oper,name,alias,jdbctype,value,valueS,valueE));
        }
        //多字段搜索
        if((plugin.equalsIgnoreCase("unionor")||plugin.equalsIgnoreCase("input-unionor"))&&StringUtils.isNotBlank(itemSearchValue)){
            //选择了type
            if(StringUtils.isNotBlank(itemSearchKey)){
                //找到对应key的search，然后拼接SQL
                for (int i = 0; i < ((List<Map<String,Object>>)search.get("children")).size(); i++) {
                    Map<String,Object> c=((List<Map<String,Object>>)search.get("children")).get(i);
                    String cname=Utils.getStringByObject(c,"item_name") ;
                    String calias=Utils.getStringByObject(c,"alias") ;
                    if(StringUtils.equalsIgnoreCase(cname,itemSearchKey)){
                        result.append( SqlUtils.like2(" and ",calias,cname,
                                OperatorEnum.LIKE , itemSearchValue ) );
                        break;
                    }
                }
            }
            else{ //没选择type
                result.append(" and ( 1=2 ");
                Set<String> nameSet=new HashSet<>();
                String[] ar=name.toUpperCase().split(",");
                nameSet.addAll(Arrays.asList(ar));
                for (int i = 0; i < ((List<Map<String,Object>>)search.get("children")).size(); i++) {
                    Map<String,Object> c=((List<Map<String,Object>>)search.get("children")).get(i);
                    String cname=Utils.getStringByObject(c,"item_name") ;
                    String calias=Utils.getStringByObject(c,"alias") ;
                    if(nameSet.contains(cname)){
                        result.append( SqlUtils.like2(" or ",calias,cname,
                                OperatorEnum.LIKE , itemSearchValue ) );
                    }
                }
                result.append(" ) ");

            }

        }
        //多字段时间搜索
        if(plugin.equalsIgnoreCase("uniondate") ){
            //没有搜索值
            if(StringUtils.isBlank(itemSearchDateValueStart)&&StringUtils.isBlank(itemSearchDateValueEnd)){
                return "";
            }
            if(search.get("children")==null){
                return "";
            }
            //时间区间 确保选择了一项
            //找到对应key的search，然后拼接SQL
            for (int i = 0; i < ((List<Map<String,Object>>)search.get("children")).size(); i++) {
                Map<String,Object> c=((List<Map<String,Object>>)search.get("children")).get(i);
                String cname=Utils.getStringByObject(c,"item_name") ;
                String calias=Utils.getStringByObject(c,"alias") ;
                if(StringUtils.equalsIgnoreCase(cname,itemSearchDateKey)){
                    result.append(   SqlUtils.transToWhere(OperatorEnum.BETWEEN.name(),
                            cname,calias, JdbcTypeEnum.DATETIME.getName(),//默认日期时间
                            "",itemSearchDateValueStart,itemSearchDateValueEnd
                    ));
                    break;
                }
            }

        }

        //
        //多字段搜索
        if(plugin.equalsIgnoreCase("union")&&StringUtils.isNotBlank(itemSearchConcatValue)){
            //选择了type
            if(StringUtils.isNotBlank(itemSearchKey)){
                //找到对应key的search，然后拼接SQL
                for (int i = 0; i < ((List<Map<String,Object>>)search.get("children")).size(); i++) {
                    Map<String,Object> c=((List<Map<String,Object>>)search.get("children")).get(i);
                    String cname=Utils.getStringByObject(c,"item_name") ;
                    String calias=Utils.getStringByObject(c,"alias") ;
                    if(StringUtils.equalsIgnoreCase(cname,itemSearchKey)){
                        result.append( SqlUtils.like2(" and ",calias,cname,
                                OperatorEnum.LIKE , itemSearchConcatValue ) );
                        break;
                    }
                }
            }
            else{ //没选择type
                result.append(" and ( 1=1 ");
                Set<String> nameSet=new HashSet<>();
                String[] ar=name.toUpperCase().split(",");
                Collections.addAll(Arrays.asList(ar));
                String concat=concat_ws(((List<Map<String,Object>>)search.get("children")));
                String[] values=StringUtils.split(StringUtils.replaceToSearch(itemSearchConcatValue),"%");
                for (int i = 0; i < values.length; i++) {
                    result.append(" and "+ concat+" like concat('%','"+values[i]+"','%')");
                }
                result.append(" ) ");
            }

        }
        return result.toString();
    }

    private static String concat_ws(List<Map<String,Object>> list){
        StringBuilder sb=new StringBuilder();
        sb.append(" concat_ws(' '");
        for (int i = 0; i < list.size(); i++) {
            Map<String,Object> c=list.get(i);
            String cname=Utils.getStringByObject(c,"item_name") ;
            String calias=Utils.getStringByObject(c,"alias") ;

            sb.append(","+SqlUtils.alias(calias,cname));
        } ;
        sb.append(")");
        return sb.toString();
    }


    public static Map<String,String> customSearchJsonToSql(String json,Map<String,Map<String,Object>> searchNameMap){
        String customJson =Utils.trimNull(json);
        Map<String,String> result=new HashMap<>();
        String customWhere="";
        String customOrder="";
        if(StringUtils.isNotBlank(customJson)){
            try {
                CustomSearchDTO customSearchDTO = JSONUtils.parseObject(customJson, CustomSearchDTO.class);
                customWhere = customWhere(customSearchDTO.getG(), customSearchDTO.getS(),searchNameMap);
                customOrder = customOrder(customSearchDTO.getO());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        result.put("customWhere",StringUtils.isBlank(customWhere)?"":"   "+Utils.trimNull(customWhere));
        result.put("customOrder"," "+Utils.trimNull(customOrder));
        return result;
    }

    private static String customOrder(List<CustomSearchOrder> o) {
        if(o==null||o.isEmpty()){
            return "";
        }
        StringBuilder order=new StringBuilder();
        for (int i = 0; i < o.size(); i++) {
            if(StringUtils.isNotBlank(o.get(i).getF())&&StringUtils.isNotBlank(o.get(i).getO())){
                order.append(", ")
                    .append(StringUtils.safeDb(o.get(i).getF()))
                    .append(" ")
                    .append(StringUtils.safeDb(o.get(i).getO()));
            }
        }
        if(order.length()>1){
            return " order by "+order.substring(1);
        }
        return "";
    }

    private static String customWhere(List<CustomSearchGroup> g, List<CustomSearchSingle> s,Map<String,Map<String,Object>> searchNameMap) {
        StringBuilder sql=new StringBuilder();
        if(Utils.isNotEmpty(s)){
            for (int i = 0; i < s.size(); i++) {
                String field=StringUtils.safeDb(s.get(i).getF());
                String type=StringUtils.safeDb(" "+s.get(i).getT()+" ");
                Map<String,Object> search=searchNameMap==null?new HashMap<>():searchNameMap.get(field);

                String jdbctype=Utils.getStringByObject(search,JsoupUtil.JDBCTYPE);
                String alias=Utils.getStringByObject(search,JsoupUtil.ALIAS);

                String w=transToWherePrePare(StringUtils.safeDb(s.get(i).getO()),
                        StringUtils.safeDb(s.get(i).getF()),alias,
                        jdbctype,StringUtils.safeDb(s.get(i).getV()),"","",false,type
                );
               if(i==0){
                   w= w.trim().replaceFirst(type ,"");
               }
                sql.append(w);
            }
        }
        if(Utils.isNotEmpty(g)){
            for (int i = 0; i < g.size(); i++) {
                if(g!=null&&Utils.isNotEmpty(g.get(i).getC())){
                    sql.append(" " + g.get(i).getT() +" ( ");
                    g.get(i).getC().forEach(item->{
                        sql.append(customWhere(item.getG(),item.getS(),searchNameMap));
                    });
                    sql.append(" )");
                }
            }
        }

        return sql.toString();
    }

}
