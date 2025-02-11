package top.ezadmin.plugins.sqlgenerate;

import top.ezadmin.common.constants.SelectKVContants;
import top.ezadmin.common.enums.JdbcTypeEnum;
import top.ezadmin.common.utils.EzDateUtils;
import top.ezadmin.common.utils.SqlUtils;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;

import java.util.Map;

public class MySqlGenerate extends SqlGenerate{
    @Override
    public String buildPageSql(String body,String groupBy) {
        StringBuilder sql=new StringBuilder();

        sql.append(body);
        sql.append(" "  );
        sql.append( generateWhere() );
        sql.append(" "  );
        if(StringUtils.isNotBlank(groupBy) ){
            sql.append(groupBy );
        }
        if(StringUtils.isNotBlank(getPage().getOrderByClause()) ){
            sql.append(getPage().getOrderByClause() );
        }
        sql.append(  " limit " + getPage().getStartRecord() + "," +  getPage().getPerPageInt());

        return sql.toString();
    }

    @Override
    public String buildCountSql(String body,String groupBy) {
        StringBuilder sql=new StringBuilder();
        //包起来的count
        Map<String,Object> core=( Map<String,Object>)getList().get("core");
        String countExpress=
                Utils.getStringByObject(core,"count_express");
        if(StringUtils.isBlank(countExpress)){
            sql.append(" select count(1) from( ");
            sql.append(body );
            sql.append(generateWhere());
            if(StringUtils.isNotBlank(groupBy) ){
                sql.append(groupBy );
            }
            sql.append(" ) a" );
        }else // 自动去掉查询条件，然后count
            if(StringUtils.equalsIgnoreCase(countExpress,"count(1)")){
                sql.append("select count(1)   "+body.substring( body.toLowerCase().indexOf("from ")));
                sql.append(generateWhere());
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
                    sql.append(generateWhere());
                    if(StringUtils.isNotBlank(groupBy) ){
                        sql.append(groupBy );
                    }
                }
        return sql.toString();
    }

    /**
     * @param union
     * @param field
     * @param jdbcType
     * @param value
     * @return
     */
    @Override
    public String eq(String union, String alias, String field, String jdbcType, String value,boolean prepare) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(union)
                .append(field)
                .append(" = ") ;
        if(prepare){
            sb.append(PREFIX);//#{A,jdbcType=xx}
            sb.append(field);
            if(JdbcTypeEnum.NUMBER.getName().equalsIgnoreCase(jdbcType)){
                sb.append(",jdbcType=NUMBER");
            }
            sb.append(SUFIX);
        }else{
            if(JdbcTypeEnum.NUMBER.getName().equalsIgnoreCase(jdbcType)){
                sb.append(value);
            }else{
                sb.append("'").append(value).append("'");
            }
        }
        return sb.toString();
    }

    /**
     * @param union
     * @param alias
     * @param field
     * @param jdbcType
     * @param value
     * @return
     */
    @Override
    public String eqAll(String union, String alias, String field, String jdbcType, String value) {
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
        String fieldName= SqlUtils.alias(alias,field);
        if(idArray.length>0){
            for (String item : idArray) {
                if (StringUtils.isBlank(item)) {
                    continue;
                }
                String valueNew= item ;
                if (JdbcTypeEnum.VARCHAR.getName().equals(jdbcType)) {
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

    /**
     * @param union
     * @param alias
     * @param field
     * @param jdbcType
     * @param valueStart
     * @param valueEnd
     * @return
     */
    @Override
    public String between(String union, String alias, String field, String jdbcType, String valueStart, String valueEnd) {
        StringBuilder sql = new StringBuilder();
        String finalStart = StringUtils.safeDb(valueStart), finalEnd = StringUtils.safeDb(valueEnd);
        switch (jdbcType){
            case "DATE_TO_NUMBER":
                valueStart=EzDateUtils.trimDateStart(valueStart);
                valueEnd=EzDateUtils.trimDateEnd(valueEnd);
                finalStart=EzDateUtils.toTimestamp(valueStart);
                finalEnd=EzDateUtils.toTimestamp(valueEnd);
                break;
            case "DATE":
                finalStart = " '" + valueStart + "' ";
                finalEnd = " '" + valueEnd + "' ";
                break;
            case "DATETIME":
                //且不包含时分秒的时候自动补充
                if(!valueStart.contains(":")){
                    valueStart = EzDateUtils.trimDateStart(valueStart);
                }
                if(!valueEnd.contains(":")){
                    valueEnd = EzDateUtils.trimDateEnd(valueEnd);
                }
                finalStart = " '" + valueStart + "' ";
                finalEnd = " '" + valueEnd + "' ";
                break;
            case "NUMBER":
            default:
                break;
        }
        if (StringUtils.isNotBlank(valueStart)) {
            sql.append(union);
            sql.append(SqlUtils.alias(alias,field));

            sql.append(" >=  " + finalStart);
        }
        if (StringUtils.isNotBlank(valueEnd)) {
            sql.append(union);
            sql.append(SqlUtils.alias(alias,field));
            sql.append(" <=  " + finalEnd);
        }
        sql.append(" ");
        return sql.toString();
    }

    /**
     * @param union
     * @param alias
     * @param field
     * @param jdbcType
     * @param value
     * @return
     */
    @Override
    public String like(String union, String alias, String field, String jdbcType, String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(union);
        sb.append(SqlUtils.alias(alias,field));
        sb.append(" like  ");
        sb.append(" concat('%','");
        sb.append(StringUtils.replaceToSearch(value));
        sb.append(  "','%')");
        return sb.toString();
    }

    /**
     * @param union
     * @param alias
     * @param field
     * @param jdbcType
     * @param value
     * @return
     */
    @Override
    public String in(String union, String alias, String field, String jdbcType, String value) {
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
        sql.append(SqlUtils.alias(alias,field));
        sql.append(" in ");
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

    @Override
    public String find_in_set(String union, String alias, String field, String jdbcType, String value) {

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
                sql.append(SqlUtils.alias(alias,field));
                sql.append(" )");
            }
        }
        sql.append(" )");
        return sql.toString();
    }


}
