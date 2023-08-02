package com.ezadmin.plugins.express;

import com.ezadmin.biz.dao.Dao;
import com.ezadmin.common.utils.JSONUtils;
import com.ezadmin.common.utils.SqlUtils;
import com.ezadmin.common.utils.Utils;
import com.ezadmin.plugins.parser.CommentsSqlParser;
import com.ezadmin.plugins.parser.parse.ResultModel;

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
