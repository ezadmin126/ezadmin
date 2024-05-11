package top.ezadmin.plugins.express;

 import top.ezadmin.common.utils.Page;
import top.ezadmin.common.utils.SqlUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.parser.CommentsSqlParser;
import top.ezadmin.plugins.parser.parse.ResultModel;

 import java.util.List;
import java.util.Map;

public class WhereOperator extends AbstractOperator {
    @Override
    public Object executeInner(Object[] objects) throws Exception {
        String sql = objects[0].toString();
        try {
            OperatorParam operatorParam=(OperatorParam) Utils.getParam();

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
            String groupBy = " " + Utils.trimNull(operatorParam.getParams().get("GROUP_BY")) + " ";
            String limit = " ", orderByClause = " ";
            if (page != null) {
                limit = " limit " + page.getStartRecord() + "," + page.getPerPageInt();
                orderByClause = page.getOrderByClause();
            }

            String finalSql = SqlUtils.buildPageSql(sql, Utils.getStringByObject(core,"count_express"), where.toString(), orderByClause, groupBy,
                    limit, operatorParam.isCount());

            ResultModel modelY = CommentsSqlParser.parse(finalSql, operatorParam.getParams());
            return modelY;
        } catch (Exception e) {
            Utils.addLog(sql, e);
            throw e;
        }
    }
}
