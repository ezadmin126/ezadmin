package top.ezadmin.plugins.express;

import top.ezadmin.dao.Dao;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.parser.CommentsSqlParser;
import top.ezadmin.plugins.parser.parse.ResultModel;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecursiveSelectOperator extends AbstractOperator {

    Logger logger = LoggerFactory.getLogger(RecursiveSelectOperator.class);

    @Override
    public Object executeInner(Object[] objects) throws Exception {
        List<Map<String,Object>> result=new ArrayList<>();
        String sql = objects[0].toString();
        OperatorParam operatorParam=(OperatorParam) Utils.getParam();
        ResultModel model = CommentsSqlParser.parse(sql, operatorParam.getParams());
        try {
            if (operatorParam.isCount()) {
                return Dao.getInstance().executeCountQuery(operatorParam.getDs(), model.getResult(), model.getParamsStatic());
            }
            List<Map<String, Object>> list = Dao.getInstance().executeQuery(operatorParam.getDs(), model.getResult(), model.getParamsStatic());
            for (int i = 0; i < list.size(); i++) {
                try {
                    recursiveLoad(result, sql, list.get(i));
                } catch (Exception e) {
                    logger.error("",e);
                }
            }
        }catch (Exception e){
            Utils.addLog(model.getResult(),e);
           // throw e;
        }
        return result;
    }

    /**
     * PARENT_ID
     */
    private void recursiveLoad(List<Map<String,Object>> result,String sql,Map<String,Object> item) throws Exception {
        result.add(item);
        OperatorParam operatorParam=(OperatorParam)Utils.getParam();
        Map<String,Object> param=new HashMap<>();
        if(StringUtils.isBlank(Utils.trimNull(item.get("ID")))){return;}
        param.put("PARENT_ID", Utils.trimNull(item.get("ID")));
        ResultModel model= CommentsSqlParser.parse(sql,param);
        List<Map<String,Object>> list= Dao.getInstance().executeQuery(operatorParam.getDs(),
                model.getResult(),model.getParamsStatic());
        if(Utils.isNotEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                try {
                    recursiveLoad(result, sql, list.get(i));
                } catch (Exception throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }


}
