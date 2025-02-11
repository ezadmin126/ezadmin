package top.ezadmin.plugins.express;

import top.ezadmin.common.utils.Utils;
import top.ezadmin.dao.Dao;
import top.ezadmin.dao.model.CustomSearchGroup;
import top.ezadmin.dao.model.CustomSearchOrder;
import top.ezadmin.dao.model.CustomSearchSingle;
import top.ezadmin.plugins.parser.CommentsSqlParser;
import top.ezadmin.plugins.parser.parse.ResultModel;
import top.ezadmin.plugins.sqlgenerate.SqlGenerate;

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
            Map<String, Object> list= operatorParam.getListDto();
            String groupBy = " " + Utils.trimNull(operatorParam.getParams().get("GROUP_BY")) + " ";
            operatorParam.getPage().setGroupBy(groupBy);
            SqlGenerate generate=  SqlGenerate.getSqlGenerate(operatorParam.getPage(),list,operatorParam.getRequestParams(),true  );
            if (operatorParam.isCount()) {
                finalSql=(generate.buildCountSql(sql,groupBy));
                modelY = CommentsSqlParser.parse(finalSql, operatorParam.getRequestParams());
                return Dao.getInstance().executeCountQuery(operatorParam.getDs(), modelY.getResult(), modelY.getParamsStatic());
            }else{
                finalSql=(generate.buildPageSql(sql,groupBy));
                modelY = CommentsSqlParser.parse(finalSql, operatorParam.getRequestParams());
                return Dao.getInstance().executeQuery(operatorParam.getDs(), modelY.getResult(), modelY.getParamsStatic());
            }
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
