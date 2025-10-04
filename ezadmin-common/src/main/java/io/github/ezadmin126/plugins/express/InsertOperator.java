package io.github.ezadmin126.plugins.express;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.ezadmin126.common.utils.Utils;
import io.github.ezadmin126.dao.Dao;
import io.github.ezadmin126.plugins.parser.CommentsSqlParser;
import io.github.ezadmin126.plugins.parser.parse.ResultModel;

public class InsertOperator extends AbstractOperator {

    Logger logger = LoggerFactory.getLogger(InsertOperator.class);

    @Override
    public Object executeInner(Object[] objects) throws Exception {
        OperatorParam operatorParam = (OperatorParam) Utils.getParam();
        String sql = objects[0].toString();
        ResultModel model = CommentsSqlParser.parse(sql, operatorParam.getParams());
        if (logger.isDebugEnabled()) {
            logger.debug("insert sql::{}", model.getResult());
        }
        return "" + Dao.getInstance().executeUpdate(operatorParam.getDs(), model.getResult(), model.getParamsStatic());

    }
}
