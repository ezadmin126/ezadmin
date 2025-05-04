package top.ezadmin.plugins.sqlgenerate;

import top.ezadmin.common.enums.JdbcTypeEnum;
import top.ezadmin.common.utils.EzDateUtils;
import top.ezadmin.common.utils.SqlUtils;
import top.ezadmin.common.utils.StringUtils;

public class OracleGenerate extends MySqlGenerate{
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
            case "DATETIME":
                //且不包含时分秒的时候自动补充
                if(!valueStart.contains(":")){
                    valueStart = EzDateUtils.trimDateStart(valueStart);
                }
                if(!valueEnd.contains(":")){
                    valueEnd = EzDateUtils.trimDateEnd(valueEnd);
                }
                if (StringUtils.isNotBlank(valueStart)) {
                    sql.append(union);
                    sql.append(SqlUtils.alias(alias,field));
                    sql.append(" >=  TO_TIMESTAMP('"+finalStart+"', 'YYYY-MM-DD HH24:MI:SS') "  );
                }
                if (StringUtils.isNotBlank(valueEnd)) {
                    sql.append(union);
                    sql.append(SqlUtils.alias(alias,field));
                    sql.append(" >=  TO_TIMESTAMP('"+finalEnd+"', 'YYYY-MM-DD HH24:MI:SS') "  );
                }
                break;
            case "DATE":
                if (StringUtils.isNotBlank(valueStart)) {
                    sql.append(union);
                    sql.append(SqlUtils.alias(alias,field));
                    sql.append(" >=  TRUNC('"+finalStart+"') "  );
                }
                if (StringUtils.isNotBlank(valueEnd)) {
                    sql.append(union);
                    sql.append(SqlUtils.alias(alias,field));
                    sql.append(" >=  TRUNC('"+finalEnd+"') + INTERVAL '1' DAY "  );
                }
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

    @Override
    public String buildPageSql(String body,String groupBy) {
        StringBuilder sql=new StringBuilder();
        sql.append("select * from ( " );
        sql.append("        select * from ( " );
        sql.append("                select ez.*,rownum rowno from ( " );
        sql.append(body);
        sql.append(" "  );
        sql.append( generateWhere() );
        sql.append("     ) ez where 1=1 " );
        sql.append(" "  );
        if(StringUtils.isNotBlank(groupBy) ){
            sql.append(groupBy );
        }
        if(StringUtils.isNotBlank(getPage().getOrderByClause()) ){
            sql.append(getPage().getOrderByClause() );
        }
        sql.append("  ) abc where abc.rowno <= "+ getPage().getEndRecord());
        sql.append(" ) def where def.rowno > "+ getPage().getStartRecord());
        return sql.toString();
    }

    public String eq(String union, String alias, String field, String jdbcType, String value,boolean prepare) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String fieldName = SqlUtils.alias(alias, field);
        sb.append(union)
                .append(fieldName)
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

}
