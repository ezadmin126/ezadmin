package top.ezadmin.plugins.express;

import top.ezadmin.common.utils.Page;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.dao.Dao;
import top.ezadmin.plugins.parser.CommentsSqlParser;
import top.ezadmin.plugins.parser.parse.ResultModel;
import top.ezadmin.plugins.sqlgenerate.SqlGenerate;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TreeSearchOperator extends AbstractOperator {

    @Override
    public Object executeInner(Object[] objects) throws Exception {
        String sql=objects[0].toString();
        String fieldName=objects[1].toString();
        String fieldIdName=objects[2].toString();
        String fieldParentIdName=objects[3].toString();
        String fieldParentIdRootValue=objects[4].toString();

        OperatorParam operatorParam=(OperatorParam) Utils.getParam();
        ResultModel modelY=null;String finalSql="";
        try {
            if(logger.isDebugEnabled()){
                logger.debug("开始执行脚本{}",sql);
            }
            Page page = operatorParam.getPage();
            StringBuilder where = new StringBuilder(" ");

            Map<String, Object> list= operatorParam.getListDto();
            String groupBy = " " + Utils.trimNull(operatorParam.getParams().get("GROUP_BY")) + " ";
            operatorParam.getPage().setGroupBy(groupBy);
            SqlGenerate generate=  SqlGenerate.getSqlGenerate(operatorParam.getPage(),list,operatorParam.getRequestParams(),true  );

            if (operatorParam.isCount()) {
                finalSql=(generate.buildCountSql(sql+" and "+fieldParentIdName+"="+fieldParentIdRootValue +" ",groupBy));
                modelY = CommentsSqlParser.parse(finalSql, operatorParam.getRequestParams());
                return Dao.getInstance().executeCountQuery(operatorParam.getDs(), modelY.getResult(), modelY.getParamsStatic());
            }else{
                finalSql=(generate.buildPageSql(sql+" and "+fieldParentIdName+"="+fieldParentIdRootValue +" ",groupBy));
                modelY = CommentsSqlParser.parse(finalSql, operatorParam.getRequestParams());
                List<Map<String, Object>> resultlist= Dao.getInstance().executeQuery(operatorParam.getDs(), modelY.getResult(), modelY.getParamsStatic());
                if(Utils.isNotEmpty(resultlist)){
                    for (int i = 0; i < resultlist.size(); i++) {
                        Map<String, Object> current=resultlist.get(i);
                        fillChild(current,sql,fieldName,fieldIdName,fieldParentIdName, groupBy,
                                operatorParam,generate);
                    }
                }
                return resultlist;
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

    void fillChild(Map<String, Object> current,String sql,String fieldName,String fieldIdName, String fieldParentIdName,
                   String groupBy,OperatorParam operatorParam,SqlGenerate generate){
        //子查询不处理where条件
        Object filedId=current.get("ID");

        String  finalSqlChild =generate.buildPageSqlWithWhere(sql,groupBy," and "+fieldParentIdName+"="+filedId +" ");
        ResultModel  modelz = CommentsSqlParser.parse(finalSqlChild, operatorParam.getRequestParams());
        try {
            List<Map<String, Object>> children = Dao.getInstance().executeQuery(operatorParam.getDs(), modelz.getResult(), modelz.getParamsStatic());
            current.put("children",children);
            current.put("id",current.get(fieldIdName));
            current.put("parentId",current.get(fieldParentIdName));
            current.put("name",current.get(fieldName));
            if(Utils.isNotEmpty(children)){
                current.put("isParent",true);
                for (int i = 0; i < children.size(); i++) {
                    Map<String, Object> child=children.get(i);
                    fillChild(child,sql,fieldName,fieldIdName,fieldParentIdName, groupBy,operatorParam,generate);
                }
            }else{
                current.put("isParent",false);
            }
        }catch (Exception e){
            logger.error("curren="+current,e);
        }
    }

}
