package top.ezadmin.plugins.express;

import top.ezadmin.dao.Dao;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.parser.CommentsSqlParser;
import top.ezadmin.plugins.parser.parse.ResultModel;

public class CountOperator extends AbstractOperator {
    public CountOperator( ) {

    }
    @Override
    public Object executeInner(Object[] objects) throws Exception {
        OperatorParam operatorParam=(OperatorParam) Utils.getParam();
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
