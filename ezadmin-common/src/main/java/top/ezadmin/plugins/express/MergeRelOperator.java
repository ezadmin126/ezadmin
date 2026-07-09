package top.ezadmin.plugins.express;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.EzBootstrap;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.dao.Dao;
import top.ezadmin.plugins.express.jdbc.MergeRelParam;
import top.ezadmin.plugins.express.jdbc.UpdateParam;
import top.ezadmin.plugins.parser.CommentsSqlParser;
import top.ezadmin.plugins.parser.MapParser;
import top.ezadmin.plugins.parser.StandardSqlParser;
import top.ezadmin.plugins.parser.parse.ResultModel;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

public class MergeRelOperator extends AbstractOperator {

    Logger logger = LoggerFactory.getLogger(MergeRelOperator.class);

    @Override
    public Object executeInner(Object[] objects) throws Exception {
        OperatorParam operatorParam = (OperatorParam) Utils.getParam();
        MergeRelParam param = (MergeRelParam) objects[0];
        ResultModel resultModelExist = CommentsSqlParser.parse(param.getExsitSql(), operatorParam.getParams());
        List<Long> existList = Dao.getInstance().executeListOneQuery(operatorParam.getDs(), resultModelExist.getResult(), resultModelExist.getParamsStatic());

        List<Long> newList = Arrays.stream(param.getNewIds().split(","))
                .map(Long::valueOf).collect(Collectors.toList());

        // 1. 创建副本，避免修改原始列表
        List<Long>       deleteIds = new ArrayList(existList);
        List<Long>        addIds = new ArrayList(newList);
        // 2. 计算差集
        // 需要删除的：在exist中，但不在new中
        deleteIds.removeAll(newList);
        // 需要新增的：在new中，但不在exist中
        addIds.removeAll(existList);
        logger.info("deleteIds: {}", deleteIds);
        logger.info("addIds: {}", addIds);
        Connection connection =null;
        try{
            connection = operatorParam.getDs().getConnection();
            connection.setAutoCommit(false);
            for (int i = 0; i < deleteIds.size(); i++) {
                Map<String, Object> params =operatorParam.getParams();
                params.put("MERGE_REL_DELETE_ID", deleteIds.get(i));
                ResultModel resultModel = CommentsSqlParser.parse(param.getDeleteSql(), params);
                Dao.getInstance().executeUpdateNotClose(connection, resultModel.getResult(), resultModel.getParamsStatic());
            }
            for (int i = 0; i < addIds.size(); i++) {
                Map<String, Object> params =operatorParam.getParams();
                params.put("MERGE_REL_INSERT_ID", addIds.get(i));
                ResultModel resultModel = CommentsSqlParser.parse(param.getInsertSql(), params);
                Dao.getInstance().executeUpdateNotClose(connection, resultModel.getResult(), resultModel.getParamsStatic());
            }
            connection.commit();
        }catch (Exception e){
            logger.error("",e );
            connection.rollback();
            return 0;
        }
        finally {
            if (connection != null) {
                connection.close();
            }
        }
        return 1;
    }

}
