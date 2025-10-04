package io.github.ezadmin126.plugins.parser;

import io.github.ezadmin126.plugins.parser.parse.ResultModel;

import java.util.Map;


/**
 * 1.${p}
 * 2.#{p}
 */
public class CommentsSqlParser {
    private CommentsSqlParser() {
        // Prevent Instantiation
    }

    public static ResultModel parse(String string, Map<String, Object> variables) {
        //匹配完成之后 ，再全局匹配
        ResultModel mod = MapParser.parse(string, variables);//sql片段匹配
        //带参数类型的匹配
        return StandardSqlParser.parse(mod.getResult(), variables);
    }
}
