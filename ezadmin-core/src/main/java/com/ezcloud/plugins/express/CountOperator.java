package com.ezcloud.plugins.express;

import com.ezcloud.dao.Dao;
import com.ezcloud.common.utils.JSONUtils;
import com.ezcloud.common.utils.Utils;
import com.ezcloud.plugins.parser.CommentsSqlParser;
import com.ezcloud.plugins.parser.parse.ResultModel;

public class CountOperator extends AbstractOperator {
    public CountOperator( ) {

    }
    @Override
    public Object executeInner(Object[] objects) throws Exception {
        OperatorParam operatorParam=(OperatorParam)Utils.getParam();
        String sql=objects[0].toString();
        ResultModel model=null;
        try {

              model = CommentsSqlParser.parse(sql, operatorParam.getParams());

            return Dao.getInstance().executeCountQuery(operatorParam.getDs(), model.getResult(), model.getParamsStatic());
        }catch (Exception e){
            Utils.addLog(JSONUtils.toJSONString(model),e);
            return 0;
        }
    }

}
