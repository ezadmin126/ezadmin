package top.ezadmin.plugins.express;

import top.ezadmin.common.utils.*;
import top.ezadmin.dao.Dao;
import top.ezadmin.dao.model.CustomSearchDTO;
import top.ezadmin.dao.model.CustomSearchGroup;
import top.ezadmin.dao.model.CustomSearchOrder;
import top.ezadmin.dao.model.CustomSearchSingle;
import top.ezadmin.plugins.parser.CommentsSqlParser;
import top.ezadmin.plugins.parser.parse.ResultModel;
import top.ezadmin.plugins.sqlog.format.FormatStyle;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SearchOperator extends AbstractOperator {

    @Override
    public Object executeInner(Object[] objects) throws Exception {
        String sql=objects[0].toString();
        OperatorParam operatorParam=(OperatorParam) Utils.getParam();
        ResultModel modelY=null;String finalSql="";
        try {
            if(logger.isDebugEnabled()){
                logger.debug("开始执行脚本{}",sql);
            }
            Page page = operatorParam.getPage();
            StringBuilder where = new StringBuilder(" ");

            Map<String, Object> list= operatorParam.getListDto();
            List<Map<String,Object>> searchList=(List<Map<String,Object>>)list.get("search");
             Map<String,Object>  core=( Map<String,Object>)list.get("core");

             if(Utils.isNotEmpty(searchList)){
                for (int i = 0; i < searchList.size(); i++) {
                    Map<String,Object> search=searchList.get(i);
                    where.append(SqlUtils.searchToSql(search,operatorParam.getRequestParams()));
                }
            }
            String customJson =Utils.trimNull(operatorParam.getRequestParams().get("customSearch"));
            String customWhere="";
            String customOrder="";
            if(StringUtils.isNotBlank(customJson)){
                try {
                    CustomSearchDTO customSearchDTO = JSONUtils.parseObject(customJson, CustomSearchDTO.class);
                    customWhere = customWhere(customSearchDTO.getG(), customSearchDTO.getS());
                    customOrder = customOrder(customSearchDTO.getO());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(StringUtils.isNotBlank(customJson)){
                where.append(customWhere);
            }

            String groupBy = " " + Utils.trimNull(operatorParam.getParams().get("GROUP_BY")) + " ";
            String limit = " ", orderByClause = " ";
            if (page != null) {
                limit = " limit " + page.getStartRecord() + "," + page.getPerPageInt();
                orderByClause = page.getOrderByClause();
            }

            if(StringUtils.isNotBlank(customOrder)){
                orderByClause=customOrder;
            }

              finalSql = SqlUtils.buildPageSql(sql,Utils.getStringByObject(core,"count_express"),
                    where.toString(), orderByClause, groupBy,
                    limit, operatorParam.isCount());

              modelY = CommentsSqlParser.parse(finalSql, operatorParam.getRequestParams());

            if (operatorParam.isCount()) {
                if(logger.isDebugEnabled()){
                    logger.debug("开始执行脚本 生成SQL count: {}", FormatStyle.BASIC.getFormatter().format(modelY.getResult()));
                }
                return Dao.getInstance().executeCountQuery(operatorParam.getDs(), modelY.getResult(), modelY.getParamsStatic());
            }
            if(logger.isDebugEnabled()){
                logger.debug("开始执行脚本 生成SQL select:{}",FormatStyle.BASIC.getFormatter().format(modelY.getResult()));
            }
            return Dao.getInstance().executeQuery(operatorParam.getDs(), modelY.getResult(), modelY.getParamsStatic());
        }catch (SQLException e){
            if(modelY!=null){
                logger.error(modelY.toString(),e);
            }
           else{
                logger.error(finalSql,e);
            }
            throw e;
        }
        catch (Exception e){
            throw e;
        }
    }

    private String customOrder(List<CustomSearchOrder> o) {
        return null;
    }

    private String customWhere(List<CustomSearchGroup> g, List<CustomSearchSingle> s) {
        return null;
    }

}
