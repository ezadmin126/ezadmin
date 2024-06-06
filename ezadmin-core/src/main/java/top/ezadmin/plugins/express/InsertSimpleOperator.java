package top.ezadmin.plugins.express;

import top.ezadmin.dao.Dao;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.express.jdbc.InsertParam;
import top.ezadmin.plugins.parser.CommentsSqlParser;
import top.ezadmin.plugins.parser.StandardSqlParser;
import top.ezadmin.plugins.parser.parse.ResultModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsertSimpleOperator extends AbstractOperator {

    Logger logger =LoggerFactory.getLogger(InsertSimpleOperator.class);

    @Override
    public Object executeInner(Object[] objects) throws Exception {
        OperatorParam operatorParam=(OperatorParam) Utils.getParam();
        InsertParam param =(InsertParam)objects[0];
        ResultModel model=generateSql(param);

        if(logger.isDebugEnabled()){
            logger.debug("InsertSimple sql::{}",model.getResult());
        }
        return ""+ Dao.getInstance().executeUpdate(operatorParam.getDs(),model.getResult(),model.getParamsStatic());
    }

    public ResultModel generateSql(InsertParam param){
        OperatorParam operatorParam=(OperatorParam) Utils.getParam();


        StringBuilder fields=new StringBuilder( );
        StringBuilder values=new StringBuilder( );

        for (int i = 0; i < param.getList().size(); i++) {
            String fieldString=param.getList().get(i).toString();
            String key=StandardSqlParser.getKey(fieldString);
            fields.append(","+key);
            values.append(","+fieldString);
//            ResultModel model = StandardSqlParser.parse(fieldString);
//            String field=model.getParams().get(0).getParamKey();
//            if(operatorParam!=null&&operatorParam.getParams()!=null&&operatorParam.getParams().containsKey(field)
//                  && StringUtils.isNotBlank(Utils.trimNull(operatorParam.getParams().get(field)))
//            ){
//                fields.append(","+field);
//                values.append(","+fieldString );
//            }else if(StringUtils.isNotBlank(Utils.trimNull(model.getParams().get(0).getParamValue())) ){
//                fields.append(","+field);
//                values.append(","+model.getParams().get(0).getParamValue() );
//            }
        }
        StringBuilder sql=new StringBuilder("insert into ");
        sql.append(param.getTable());
        sql.append("(");
        sql.append(fields.substring(1));
        sql.append(")");
        sql.append("values");
        sql.append("(");
        sql.append(values.substring(1));
        sql.append(")");
        ResultModel model= CommentsSqlParser.parse(sql.toString(),operatorParam.getParams());
        return model;
    }
}
