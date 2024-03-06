package top.ezadmin.plugins.express;

import top.ezadmin.dao.Dao;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.parser.CommentsSqlParser;
import top.ezadmin.plugins.parser.parse.ResultModel;

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
        OperatorParam operatorParam=(OperatorParam) Utils.getParam();
        String sql=objects[0].toString();
        ResultModel model= CommentsSqlParser.parse(sql,operatorParam.getParams());
        return Dao.getInstance().executeUpdate(operatorParam.getDs(),model.getResult(),model.getParamsStatic());

    }
}
