package io.github.ezadmin126;

import org.junit.Test;
import top.ezadmin.EzBootstrap;
import top.ezadmin.plugins.EzFastJson2Impl;
import top.ezadmin.plugins.EzSqlParserImpl;

public class SqlParserTest {

    static {
        EzBootstrap.config().setEzJson(new EzFastJson2Impl());
        EzBootstrap.config().setEzSqlParser(new EzSqlParserImpl());
    }

    @Test
    public void test1() throws Exception {
        String sql = """
                select A.ID ID,COMPANY_ID,DELETE_FLAG,A.LANG_KEY 语言代码,LANG_VALUE 翻译文本,LANG_DESC 描述,ADD_TIME
                    添加时间,ADD_ID,update_time ,update_id from T_SYS_I18N A 
                                 GROUP by ID order by add_time desc  limit 1,10
                """;
        //  System.out.println(EzBootstrap.config().getEzSqlParser().sqlToList(sql,"test","ds"));

        System.out.println(EzBootstrap.config().getEzSqlParser().sqlToForm(sql, "test", "ds"));
    }
}
