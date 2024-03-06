package top.ezadmin.plugins.express;

import top.ezadmin.dao.Dao;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.parser.CommentsSqlParser;
import top.ezadmin.plugins.parser.parse.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class UpdateOperator extends AbstractOperator {

    Logger logger =LoggerFactory.getLogger(UpdateOperator.class);

    @Override
    public Object executeInner(Object[] objects) throws Exception {
        String sql=objects[0].toString();
        OperatorParam operatorParam=(OperatorParam) Utils.getParam();
        ResultModel model= CommentsSqlParser.parse(sql,operatorParam.getParams());
        try {
            logger.info("update{}",sql);
            return Dao.getInstance().executeUpdate(operatorParam.getDs(),model.getResult(),model.getParamsStatic());
        } catch (SQLException throwables) {
            logger.error(sql+model.getResult(),throwables);
            throw throwables;
        }

    }
}
