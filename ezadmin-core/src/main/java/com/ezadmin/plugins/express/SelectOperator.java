package com.ezadmin.plugins.express;

import com.ezadmin.biz.dao.Dao;
import com.ezadmin.common.utils.Utils;
import com.ezadmin.plugins.parser.CommentsSqlParser;
import com.ezadmin.plugins.parser.parse.ResultModel;

public class SelectOperator extends AbstractOperator {



    @Override
    public Object executeInner(Object[] objects) throws Exception {
        OperatorParam operatorParam=(OperatorParam)Utils.getParam();
        String sql=objects[0].toString();

        ResultModel model= CommentsSqlParser.parse(sql,operatorParam.getParams());
        if(operatorParam.isCount()){
            return Dao.getInstance().executeCountQuery(operatorParam.getDs(),model.getResult(),model.getParamsStatic());
        }
        try {
            return Dao.getInstance().executeQuery(operatorParam.getDs(), model.getResult(), model.getParamsStatic());
        }catch (Exception e){
            Utils.addLog(model.toString(),e);
            return null;
        }
    }


}
