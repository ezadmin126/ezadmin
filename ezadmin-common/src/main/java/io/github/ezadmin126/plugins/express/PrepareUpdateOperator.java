package io.github.ezadmin126.plugins.express;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.ezadmin126.common.utils.Utils;
import io.github.ezadmin126.dao.Dao;

import java.sql.SQLException;

public class PrepareUpdateOperator extends AbstractOperator {

    Logger logger = LoggerFactory.getLogger(PrepareUpdateOperator.class);


    @Override
    public Object executeInner(Object[] objects) throws Exception {
        String sql = objects[0].toString();
        Object[] params = (Object[]) objects[1];
        try {
            OperatorParam operatorParam = (OperatorParam) Utils.getParam();
            return Dao.getInstance().executeUpdate(operatorParam.getDs(), sql, params);
        } catch (SQLException throwables) {
            logger.error(sql, throwables);
            throw throwables;
        }

    }
}
