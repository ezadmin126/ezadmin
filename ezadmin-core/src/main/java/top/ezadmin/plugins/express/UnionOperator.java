package top.ezadmin.plugins.express;

import top.ezadmin.dao.Dao;
 import top.ezadmin.common.utils.Page;
import top.ezadmin.common.utils.SqlUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.parser.CommentsSqlParser;
import top.ezadmin.plugins.parser.parse.ResultModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UnionOperator extends AbstractOperator {



    @Override
    public Object executeInner(Object[] objects) throws Exception {
        OperatorParam operatorParam=(OperatorParam) Utils.getParam();
        StringBuilder unionSql=new StringBuilder();
        if(objects==null||objects.length!=6){
            return new ArrayList<>();
        }
        try {
         String sql1= Utils.trimNull(objects[0]);
         String group1=Utils.trimNull(objects[1]);
         String order1=Utils.trimNull(objects[2]);
         String sql2=Utils.trimNull(objects[3]);
         String group2=Utils.trimNull(objects[4]);
         String order2=Utils.trimNull(objects[5]);

        ResultModel whereModel1=where(sql1,group1,order1);
        ResultModel whereModel2=where(sql2,group2,order2);

        unionSql.append(whereModel1.getResult());
        unionSql.append(" UNION ALL ");
        unionSql.append(whereModel2.getResult());

        Page page = operatorParam.getPage();
        String groupBy = " " + Utils.trimNull(operatorParam.getParams().get("GROUP_BY")) + " ";
        String limit = " ", orderByClause = " ";
        if (page != null) {
            limit = " limit " + page.getStartRecord() + "," + page.getPerPageInt();
            orderByClause = page.getOrderByClause();
        }

        String finalSql = SqlUtils.buildPageSql(unionSql.toString(), " "," ", orderByClause, groupBy,
                limit, operatorParam.isCount());

        //参数合并
        whereModel1.getParamStaticList().addAll(whereModel2.getParamStaticList());

        if (operatorParam.isCount()) {
            return Dao.getInstance().executeCountQuery(operatorParam.getDs(), finalSql, whereModel1.getParamsStatic());
        }
//        if (Utils.getLog() != null) {
//            Utils.addLog("生成解析完成的Sql#######################"+finalSql);
//        }
        return Dao.getInstance().executeQuery(operatorParam.getDs(), finalSql, whereModel1.getParamsStatic());
    }catch (Exception e){
            Utils.addLog(unionSql.toString(),e);
            throw e;
        }
    }
    public ResultModel where(String sql,String group,String order){
        OperatorParam operatorParam=(OperatorParam)Utils.getParam();
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



        String finalSql = SqlUtils.buildPageSql(sql,Utils.getStringByObject(core,"count_express"),where.toString(), order, group,
                " ", false);

        ResultModel model = CommentsSqlParser.parse(finalSql, operatorParam.getParams());
        return model;
    }
}
