package io.github.ezadmin126.plugins.express;

import io.github.ezadmin126.common.utils.JSONUtils;
import io.github.ezadmin126.common.utils.Utils;
import io.github.ezadmin126.dao.Dao;
import io.github.ezadmin126.plugins.parser.CommentsSqlParser;
import io.github.ezadmin126.plugins.parser.parse.ResultModel;

public class CountOperator extends AbstractOperator {
    public CountOperator() {

    }

    @Override
    public Object executeInner(Object[] objects) throws Exception {
        OperatorParam operatorParam = (OperatorParam) Utils.getParam();
        String sql = objects[0].toString();
        ResultModel model = null;
        try {

            model = CommentsSqlParser.parse(sql, operatorParam.getParams());

            return Dao.getInstance().executeCountQuery(operatorParam.getDs(), model.getResult(), model.getParamsStatic());
        } catch (Exception e) {
            Utils.addLog(JSONUtils.toJSONString(model), e);
            return 0;
        }
    }

}
