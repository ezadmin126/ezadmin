package io.github.ezadmin126.plugins.express;

import io.github.ezadmin126.common.utils.Utils;
import io.github.ezadmin126.dao.Dao;
import io.github.ezadmin126.plugins.parser.CommentsSqlParser;
import io.github.ezadmin126.plugins.parser.parse.ResultModel;

public class JoinOperator extends AbstractOperator {


    @Override
    public Object executeInner(Object[] objects) throws Exception {
//        List<Map<String,Object>> data1=(List<Map<String,Object>>)objects[0];
//        List<Map<String,Object>> data2=(List<Map<String,Object>>)objects[1];
//        List<Map<String,Object>>
//
//
//        for (int i = 0; i < data1.size(); i++) {
//
//        }
        OperatorParam operatorParam = (OperatorParam) Utils.getParam();
        String sql = objects[0].toString();
        ResultModel model = CommentsSqlParser.parse(sql, operatorParam.getParams());
        return Dao.getInstance().executeUpdate(operatorParam.getDs(), model.getResult(), model.getParamsStatic());

    }
}
