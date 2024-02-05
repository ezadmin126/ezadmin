package com.ezcloud.plugins.express;

import com.ezcloud.dao.Dao;
import com.ezcloud.common.utils.JSONUtils;
import com.ezcloud.common.utils.Utils;
import com.ezcloud.plugins.express.jdbc.UpdateParam;
import com.ezcloud.plugins.parser.CommentsSqlParser;
import com.ezcloud.plugins.parser.StandardSqlParser;
import com.ezcloud.plugins.parser.parse.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateSimpleOperator extends AbstractOperator {

    Logger logger =LoggerFactory.getLogger(UpdateSimpleOperator.class);

    @Override
    public Object executeInner(Object[] objects) throws Exception {
        String sql=objects[0].toString();
        OperatorParam operatorParam=(OperatorParam) Utils.getParam();
        UpdateParam param =(UpdateParam)objects[0];
        ResultModel model=generateSql(param);
         try {
            if(logger.isDebugEnabled()){
                logger.debug("update{}",sql);
            }
            return Dao.getInstance().executeUpdate(operatorParam.getDs(),model.getResult(),model.getParamsStatic());
        } catch (Exception throwables) {
            logger.error(JSONUtils.toJSONString(objects) +model.getResult(),throwables);
            throw throwables;
        }

    }
    private ResultModel generateSql(UpdateParam updateParam){
        OperatorParam operatorParam=(OperatorParam) Utils.getParam();


        StringBuilder fieldsvalues=new StringBuilder( );

        for (int i = 0; i < updateParam.getList().size() ; i++) {
            String fieldString=updateParam.getList().get(i).toString();//#{username}
            ResultModel model = StandardSqlParser.parse(fieldString);
            String field=model.getParams().get(0).getParamKey();//username
            Object value=model.getParams().get(0).getParamValue();;
            if(operatorParam!=null&&operatorParam.getParams()!=null&&operatorParam.getParams().containsKey(field)){
                fieldsvalues.append(","+field);
                fieldsvalues.append("="+fieldString);
            }else if(value!=null){
                fieldsvalues.append(","+field);
                fieldsvalues.append("="+value);
            }
        }
        StringBuilder sql=new StringBuilder("update  ");
        sql.append(updateParam.getTable());
        sql.append(" set ");

        sql.append(fieldsvalues.substring(1));
        sql.append(" ");
        sql.append(updateParam.getWhere());
        ResultModel model= CommentsSqlParser.parse(sql.toString(),operatorParam.getParams());
        return model;
    }
}
