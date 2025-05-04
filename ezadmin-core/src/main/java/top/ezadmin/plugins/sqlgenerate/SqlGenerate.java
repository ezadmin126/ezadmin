package top.ezadmin.plugins.sqlgenerate;

import top.ezadmin.common.enums.JdbcTypeEnum;
import top.ezadmin.common.enums.OperatorEnum;
import top.ezadmin.common.enums.ParamNameEnum;
import top.ezadmin.common.utils.*;
import top.ezadmin.dao.model.CustomSearchDTO;
import top.ezadmin.dao.model.CustomSearchGroup;
import top.ezadmin.dao.model.CustomSearchOrder;
import top.ezadmin.dao.model.CustomSearchSingle;

import java.util.*;

public abstract  class SqlGenerate {
    public final static String PREFIX = "#{";
    public final static String SUFIX = "}";
   private Page page;
   private Map<String, Object> list;
   private Map<String, Object> request;

    /**
     * 获取sql生成器
     * @param page 分页对象
     * @param list 列表配置
     * @param request 请求参数
     * @return sql
     */
    public static  SqlGenerate getSqlGenerate(Page page,Map<String, Object> list,Map<String, Object> request,boolean prepare){
        String dbType=page.getDialect();
        SqlGenerate generate=null;
        if("oracle".equalsIgnoreCase(dbType)){
            generate= new OracleGenerate();
        }else{
            //默认mysql
            generate= new MySqlGenerate();
        }
        generate.page=page;
        generate.list=list;
        generate.request=request;
        return generate;
    }

    public  String generateWhere(){
        StringBuilder where = new StringBuilder(" ");
        List<Map<String,Object>> searchList=(List<Map<String,Object>>)list.get("search");
        Map<String,Map<String,Object>> searchNameMap=new HashMap<>();
        if(Utils.isNotEmpty(searchList)){
            for (int i = 0; i < searchList.size(); i++) {
                Map<String,Object> search=searchList.get(i);
                String name=Utils.getStringByObject(search, JsoupUtil.ITEM_NAME);
                searchNameMap.put(name,search);
                where.append( searchToSql(" and ",search ));
            }
        }
        //高级搜索
        String customJson =Utils.trimNull(getRequest().get("customSearch"));
        Map<String,String> customSearch= customSearchJsonToSql(customJson,searchNameMap);

        if(customSearch.containsKey("customWhere")){
            where.append(customSearch.get("customWhere"));
        }
        return where.toString();
    }

    public  String searchToSql(String union,Map<String,Object> search ) {

        StringBuilder result=new StringBuilder();
        String plugin=Utils.getStringByObject(search,JsoupUtil.TYPE);
        String jdbctype=Utils.getStringByObject(search,JsoupUtil.JDBCTYPE);
        String oper=Utils.trimEmptyDefault(Utils.getStringByObject(search,JsoupUtil.OPER),OperatorEnum.EQ.getOperC());
        String name=Utils.getStringByObject(search,JsoupUtil.ITEM_NAME);
        String alias=Utils.getStringByObject(search,JsoupUtil.ALIAS);
        //插件单独处理，纯隐藏域，不处理
        if(plugin.equalsIgnoreCase("hidden-nowhere")||jdbctype.equalsIgnoreCase("BODY")){
            return result.toString();
        }
        //多字段合并后搜索
        String itemSearchConcatValue=Utils.getStringByObject(search,ParamNameEnum.itemSearchConcatValue.getName());

        //多字段时间搜索
        String itemSearchDateKey=Utils.getStringByObject(search,ParamNameEnum.itemSearchDateKey.getName());
        String itemSearchDateValueStart=Utils.getStringByObject(search,ParamNameEnum.itemSearchDateValueStart.getName());
        String itemSearchDateValueEnd=Utils.getStringByObject(search,ParamNameEnum.itemSearchDateValueEnd.getName());
        //多字段搜索
        if((plugin.equalsIgnoreCase("unionor")||plugin.equalsIgnoreCase("input-unionor")) ){
            // 多字段搜索
            String itemSearchKey=Utils.getStringByObject(search, ParamNameEnum.itemSearchKey.getName() );
            String itemSearchValue=Utils.getStringByObject(search,ParamNameEnum.itemSearchValue.getName());
            if(StringUtils.isBlank(itemSearchValue)){
                return "";
            }
            //选择了type
            if(StringUtils.isNotBlank(itemSearchKey)){
                //找到对应key的search，然后拼接SQL
                for (int i = 0; i < ((List<Map<String,Object>>)search.get("children")).size(); i++) {
                    Map<String,Object> c=((List<Map<String,Object>>)search.get("children")).get(i);
                    String cname=Utils.getStringByObject(c,"item_name") ;
                    String calias=Utils.getStringByObject(c,"alias") ;
                    if(StringUtils.equalsIgnoreCase(cname,itemSearchKey)){
                        result.append(like(" and ", calias,cname,
                                jdbctype , itemSearchValue ) );
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
                        result.append(like(" or ", calias,cname,
                                jdbctype , itemSearchValue ) );
                    }
                }
                result.append(" ) ");

            }
            return result.toString();
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
                    result.append( between(" and ",
                             calias,cname, JdbcTypeEnum.DATETIME.getName(),//默认日期时间
                             itemSearchDateValueStart,itemSearchDateValueEnd
                    ));
                    break;
                }
            }
            return result.toString();
        }
        //多字段搜索
        if(plugin.equalsIgnoreCase("union")&&StringUtils.isNotBlank(itemSearchConcatValue)){
            String itemSearchKey=Utils.getStringByObject(search, ParamNameEnum.itemSearchKey.getName() );
            //选择了type
            if(StringUtils.isNotBlank(itemSearchKey)){
                //找到对应key的search，然后拼接SQL
                for (int i = 0; i < ((List<Map<String,Object>>)search.get("children")).size(); i++) {
                    Map<String,Object> c=((List<Map<String,Object>>)search.get("children")).get(i);
                    String cname=Utils.getStringByObject(c,"item_name") ;
                    String calias=Utils.getStringByObject(c,"alias") ;
                    if(StringUtils.equalsIgnoreCase(cname,itemSearchKey)){
                        result.append(transOneFileSql(union,alias,name,itemSearchConcatValue,"","",
                                jdbctype,OperatorEnum.LIKE.getOperC(),true));
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
                    result.append(transOneFileSql(union,"",concat,values[i],"","",
                            jdbctype,OperatorEnum.LIKE.getOperC(),true));
                }
                result.append(" ) ");
            }
            return result.toString();
        }

        //普通字段
        String value=Utils.getStringByObject(search,ParamNameEnum.itemParamValue.getName());
        String valueS=Utils.getStringByObject(search,ParamNameEnum.itemParamValueStart.getName());
        String valueE=Utils.getStringByObject(search,ParamNameEnum.itemParamValueEnd.getName());
        return transOneFileSql(union, alias,name,value,valueS,valueE,jdbctype,oper,true);
    }
    private String transOneFileSql(String union,String alias
            ,String field,String value,String valueS,String valueE,String jdbcType, String oper,boolean prepare){
        String  result="";
        OperatorEnum operatorEnum = OperatorEnum.match(oper);
        switch (operatorEnum) {
            case EQ:
            case NE:
            case LTE:
            case GTE:
                result = eq(union,alias,field, jdbcType,value,prepare);
                break;
            case BETWEEN:
                result = between(union,alias,field, jdbcType,valueS,valueE);
                break;
            case LIKE:
            case NOTLIKE:
                result = like(union,alias,field, jdbcType,value);
                break;
            case IN:
            case NOT_IN:
                result = in(union,alias,field, jdbcType,value);
                break;
            case ALL_FIND_IN_SET:
                result = find_in_set(union,alias,field, jdbcType,value);
                break;
            case ALL_EQ:
                result = eqAll(union,alias,field, jdbcType,value);
                break;
            case ALL_LIKE:
                //todo
            default:
                break;
        }
        return result.toString();
    }

    public abstract String buildPageSql(String sql,String groupBy);
    public abstract String buildPageSqlWithWhere(String sql,String groupBy,String where);
    public abstract String buildCountSql(String sql,String groupBy);

    /**
     *
     * @param union
     * @param field
     * @param jdbcType
     * @param value
     * @return sql
     */
    public abstract String eq(String union, String alias,String field,String jdbcType,String value,boolean prepare);

    /**
     *
     * @param union
     * @param field
     * @param jdbcType
     * @param value
     * @return sql
     */
    public abstract String eqAll(String union, String alias,String field,String jdbcType,String value);

    /**
     *
     * @param union
     * @param field
     * @param jdbcType
     * @param valueS
     * @param valueE
     * @return sql
     */
    public abstract String between(String union, String alias,String field,String jdbcType,String valueS,String valueE);

    /**
     * @param union
     * @param field
     * @param jdbcType
     * @param value
     * @return sql
     */
    public abstract String like(String union, String alias,String field,String jdbcType,String value  );

    /**
     *
     * @param union
     * @param field
     * @param jdbcType
     * @param value
     * @return sql
     */
    public abstract String in(String union, String alias,String field,String jdbcType,String value );
    public abstract String find_in_set(String union, String alias,String field,String jdbcType,String value );
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

    public Page getPage() {
        return page;
    }

    public Map<String, Object> getList() {
        return list;
    }

    public Map<String, Object> getRequest() {
        return request;
    }
    public  Map<String,String> customSearchJsonToSql(String json,Map<String,Map<String,Object>> searchNameMap){
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

    private  String customOrder(List<CustomSearchOrder> o) {
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

    private  String customWhere(List<CustomSearchGroup> g, List<CustomSearchSingle> s, Map<String,Map<String,Object>> searchNameMap) {
        StringBuilder sql=new StringBuilder();
        if(Utils.isNotEmpty(s)){
            for (int i = 0; i < s.size(); i++) {
                String field=StringUtils.safeDb(s.get(i).getF());
                String type=StringUtils.safeDb(" "+s.get(i).getT()+" ");
                Map<String,Object> search=searchNameMap==null?new HashMap<>():searchNameMap.get(field);

                String jdbctype=Utils.getStringByObject(search,JsoupUtil.JDBCTYPE);
                String alias=Utils.getStringByObject(search,JsoupUtil.ALIAS);

                String w=
                        transOneFileSql(type,  alias ,field,
                                StringUtils.safeDb(s.get(i).getV()),"","",  jdbctype,
                                StringUtils.safeDb(s.get(i).getO()),false);
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
