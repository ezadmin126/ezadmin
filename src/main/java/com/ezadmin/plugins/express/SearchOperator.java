package com.ezadmin.plugins.express;

import com.ezadmin.biz.dao.Dao;
import com.ezadmin.biz.model.EzSearchModel;
import com.ezadmin.common.EzAdminRuntimeException;
import com.ezadmin.common.utils.*;
import com.ezadmin.plugins.parser.CommentsSqlParser;
import com.ezadmin.plugins.parser.parse.ResultModel;
import com.ezadmin.plugins.sqlog.format.FormatStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class SearchOperator extends AbstractOperator {

    @Override
    public Object executeInner(Object[] objects) throws Exception {
        String sql=objects[0].toString();
        OperatorParam operatorParam=(OperatorParam)Utils.getParam();
        ResultModel modelY=null;String finalSql="";
        try {
            if(logger.isDebugEnabled()){
                logger.debug("开始执行脚本{}",sql);
            }
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

              finalSql = SqlUtils.buildPageSql(sql,operatorParam.getListDTO().getCountExpress(),
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

}
