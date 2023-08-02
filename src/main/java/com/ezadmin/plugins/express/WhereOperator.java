package com.ezadmin.plugins.express;

import com.ezadmin.biz.model.EzSearchModel;
import com.ezadmin.common.utils.Page;
import com.ezadmin.common.utils.SqlUtils;
import com.ezadmin.common.utils.StringUtils;
import com.ezadmin.common.utils.Utils;
import com.ezadmin.plugins.parser.CommentsSqlParser;
import com.ezadmin.plugins.parser.parse.ResultModel;

import java.util.List;

public class WhereOperator extends AbstractOperator {



    @Override
    public Object executeInner(Object[] objects) throws Exception {
        String sql = objects[0].toString();
        try {
            OperatorParam operatorParam=(OperatorParam)Utils.getParam();

            Page page = operatorParam.getPage();
            StringBuilder where = new StringBuilder(" ");
            List<EzSearchModel> modelList=operatorParam.getListDTO().getSearchItemList();
            if(Utils.isNotEmpty(modelList)){
                for (int i = 0; i < modelList.size(); i++) {
                    where.append(modelList.get(i).sql());
                }
            }
            String groupBy = " " + Utils.trimNull(operatorParam.getParams().get("GROUP_BY")) + " ";
            String limit = " ", orderByClause = " ";
            if (page != null) {
                limit = " limit " + page.getStartRecord() + "," + page.getPerPageInt();
                orderByClause = page.getOrderByClause();
            }

            String finalSql = SqlUtils.buildPageSql(sql, operatorParam.getListDTO().getCountExpress(), where.toString(), orderByClause, groupBy,
                    limit, operatorParam.isCount());

            ResultModel modelY = CommentsSqlParser.parse(finalSql, operatorParam.getParams());

//            if (Utils.getLog() != null) {
//                Utils.addLog("生成解析完成的Sql #######################" + model.getResult());
//            }
            return modelY;
        } catch (Exception e) {
            Utils.addLog(sql, e);
            throw e;
        }
    }
}
