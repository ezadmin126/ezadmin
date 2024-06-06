package top.ezadmin.plugins.parser;

import top.ezadmin.plugins.parser.parse.GenericTokenParser;
import top.ezadmin.plugins.parser.parse.ResultModel;
import top.ezadmin.plugins.parser.parse.TokenHandler;
import top.ezadmin.common.utils.StringUtils;

import java.util.Map;


/**
 * 1.${p}
 * 2.#{p}

 */
public class CommentsSqlParser {
    private CommentsSqlParser() {
        // Prevent Instantiation
    }
    private static String startFixStr = "/*^";
    private static String endFixStr = "$*/";

    public static ResultModel parse(String string, Map<String,Object> variables) {
//        ResultModel model2=new ResultModel();
//        if(variables==null|| variables.isEmpty()){
//            model2.setResult(string);
//            return model2;
//        }
//        if(string.indexOf(startFixStr)>=0){
//            SqlCommentTokenHandler handler = new SqlCommentTokenHandler(model2,variables);
//            GenericTokenParser parser = new GenericTokenParser(startFixStr, endFixStr, handler);
//            string= parser.parse(string);
//        }
        //匹配完成之后 ，再全局匹配

        ResultModel mod= MapParser.parse(string,variables);//sql片段匹配
        //带参数类型的匹配
        return StandardSqlParser.parse(mod.getResult(),variables);
//        model2.setResult(model3.getResult());
//        for (int i = 0; i < model3.getParams().size(); i++) {
//            model2.addParam(model3.getParams().get(i));
//        }
    }

    private static class SqlCommentTokenHandler implements TokenHandler {
        private final Map<String,Object> variables;
        ResultModel model;

        private SqlCommentTokenHandler(ResultModel model,Map<String,Object> variables) {
            this.variables = variables;
            this.model=model;
        }


        @Override
        public String handleToken(String content) {
            if(StringUtils.isBlank(content)){
                return "";
            }
            //通过${}匹配后的结果
            ResultModel modelDyna=MapParser.parse(content,variables);

            ResultModel modelStand=StandardSqlParser.parse(modelDyna.getResult(),variables);

            if(modelDyna.getParams().size()>0||modelStand.getParams().size()>0){
                //只接收modelStand的参数
                for (int i = 0; i < modelStand.getParams().size(); i++) {
                    model.addParam(modelStand.getParams().get(i));
                }
                return modelStand.getResult();
            }
            return "";
        }
    }

}
