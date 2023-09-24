package com.ezadmin.plugins.express;

import com.ezadmin.dao.Dao;
import com.ezadmin.common.utils.Utils;
import com.ezadmin.plugins.parser.CommentsSqlParser;
import com.ezadmin.plugins.parser.parse.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsertOperator extends AbstractOperator {

    Logger logger =LoggerFactory.getLogger(InsertOperator.class);

    @Override
    public Object executeInner(Object[] objects) throws Exception {
        OperatorParam operatorParam=(OperatorParam) Utils.getParam();
        String sql=objects[0].toString();
        ResultModel model= CommentsSqlParser.parse(sql,operatorParam.getParams());
        if(logger.isDebugEnabled()){
            logger.debug("insert sql::{}",model.getResult());
        }
        return ""+ Dao.getInstance().executeUpdate(operatorParam.getDs(),model.getResult(),model.getParamsStatic());

    }
}
