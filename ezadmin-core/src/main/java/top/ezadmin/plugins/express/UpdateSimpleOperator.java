package top.ezadmin.plugins.express;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.EzClientBootstrap;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.dao.Dao;
import top.ezadmin.plugins.express.jdbc.UpdateParam;
import top.ezadmin.plugins.parser.CommentsSqlParser;
import top.ezadmin.plugins.parser.StandardSqlParser;
import top.ezadmin.plugins.parser.parse.ResultModel;

public class UpdateSimpleOperator extends AbstractOperator {

    Logger logger =LoggerFactory.getLogger(UpdateSimpleOperator.class);

    @Override
    public Object executeInner(Object[] objects) throws Exception {
        OperatorParam operatorParam=(OperatorParam) Utils.getParam();
        UpdateParam param =(UpdateParam)objects[0];
        ResultModel model=generateSql(param);
         try {
             if(objects.length>1){
                 if(Utils.isTrue(objects[1])){
                     EzClientBootstrap.instance().getCache().clear();
                 }
             }
            return Dao.getInstance().executeUpdate(operatorParam.getDs(),model.getResult(),model.getParamsStatic());
        } catch (Exception throwables) {
            logger.error(JSONUtils.toJSONString(objects) +model.getResult(),throwables);
            throw throwables;
        }

    }
    public ResultModel generateSql(UpdateParam updateParam){
        OperatorParam operatorParam=(OperatorParam) Utils.getParam();
        StringBuilder fieldsvalues=new StringBuilder( );
        //也就是只要在param里面add了，就一定在sql中存在
        for (int i = 0; i < updateParam.getList().size() ; i++) {
            String fieldString=updateParam.getList().get(i).toString();//#{username}
            String key=StandardSqlParser.getKey(fieldString);
            fieldsvalues.append(","+key);
            fieldsvalues.append("="+fieldString);
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
