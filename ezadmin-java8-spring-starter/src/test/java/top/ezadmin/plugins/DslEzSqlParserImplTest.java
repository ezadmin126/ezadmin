//package top.ezadmin.plugins;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.BeforeEach;
//
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.util.regex.Pattern;
//
//public class DslEzSqlParserImplTest {
//
//    private DslEzSqlParserImpl parser;
//
//    @BeforeEach
//    void setUp() {
//        parser = new DslEzSqlParserImpl();
//    }
//
//    @Test
//    void testSqlToListBasic() throws Exception {
//        String sql = "SELECT id, name, email, status FROM user WHERE status = 1";
//        String listId = "userList";
//        String datasource = "dataSource";
//
//        String result = parser.sqlToList(sql, listId, datasource);
//
//        assertNotNull(result);
//        assertTrue(isValidJson(result));
//        assertTrue(result.contains("userList"));
//        assertTrue(result.contains("id"));
//        assertTrue(result.contains("name"));
//        assertTrue(result.contains("email"));
//        assertTrue(result.contains("status"));
//    }
//
//    @Test
//    void testSqlToListWithAlias() throws Exception {
//        String sql = "SELECT u.id AS 用户ID, u.name AS 用户姓名, u.email AS 邮箱 FROM user u WHERE u.status = 1";
//        String listId = "userListAlias";
//        String datasource = "dataSource";
//
//        String result = parser.sqlToList(sql, listId, datasource);
//
//        assertNotNull(result);
//        assertTrue(isValidJson(result));
//        assertTrue(result.contains("用户ID"));
//        assertTrue(result.contains("用户姓名"));
//        assertTrue(result.contains("邮箱"));
//    }
//
//    @Test
//    void testSqlToListWithDateTimeField() throws Exception {
//        String sql = "SELECT id, name, created_time AS 创建时间, updated_time AS 更新时间 FROM user";
//        String listId = "userListTime";
//        String datasource = "dataSource";
//
//        String result = parser.sqlToList(sql, listId, datasource);
//
//        assertNotNull(result);
//        assertTrue(isValidJson(result));
//        assertTrue(result.contains("创建时间"));
//        assertTrue(result.contains("更新时间"));
//        // 时间字段应该有相应的格式化配置
//        assertTrue(result.contains("yyyy-MM-dd HH:mm:ss"));
//    }
//
//    @Test
//    void testSqlToListWithYesNoField() throws Exception {
//        String sql = "SELECT id, name, is_active AS 是否激活, is_deleted AS 是否删除 FROM user";
//        String listId = "userListYesNo";
//        String datasource = "dataSource";
//
//        String result = parser.sqlToList(sql, listId, datasource);
//
//        assertNotNull(result);
//        assertTrue(isValidJson(result));
//        assertTrue(result.contains("是否激活"));
//        assertTrue(result.contains("是否删除"));
//        // 是/否字段应该配置为选择框
//        assertTrue(result.contains("tdSelect"));
//    }
//
//    @Test
//    void testSqlToListErrorOnAllColumns() throws Exception {
//        String sql = "SELECT * FROM user";
//        String listId = "userListError";
//        String datasource = "dataSource";
//
//        String result = parser.sqlToList(sql, listId, datasource);
//
//        // 当使用 * 通配符时，应该返回错误
//        assertTrue(result.contains("\"success\":false"));
//        assertTrue(result.contains("请指定字段名及字段中文别名"));
//    }
//
//    @Test
//    void testSqlToListWithFunction() throws Exception {
//        String sql = "SELECT id, name, COUNT(*) AS 记录总数, MAX(created_time) AS 最新时间 FROM user GROUP BY id, name";
//        String listId = "userListFunc";
//        String datasource = "dataSource";
//
//        String result = parser.sqlToList(sql, listId, datasource);
//
//        assertNotNull(result);
//        assertTrue(isValidJson(result));
//        assertTrue(result.contains("记录总数"));
//        assertTrue(result.contains("最新时间"));
//    }
//
//    @Test
//    void testSqlToListStructure() throws Exception {
//        String sql = "SELECT id AS ID, name AS 名称, email AS 邮箱, status AS 状态 FROM user WHERE status = 1 ORDER BY id";
//        String listId = "userListStruct";
//        String datasource = "dataSource";
//
//        String result = parser.sqlToList(sql, listId, datasource);
//
//        assertNotNull(result);
//        assertTrue(isValidJson(result));
//
//        // 验证JSON结构包含必需的字段
//        assertTrue(result.contains("\"id\":\"userListStruct\""));
//        assertTrue(result.contains("\"name\":\"userListStruct\""));
//        assertTrue(result.contains("column"));
//        assertTrue(result.contains("search"));
//        assertTrue(result.contains("tableButton"));
//        assertTrue(result.contains("rowButton"));
//        assertTrue(result.contains("express"));
//
//        // 验证字段配置
//        assertTrue(result.contains("名称"));
//        assertTrue(result.contains("邮箱"));
//        assertTrue(result.contains("状态"));
//
//        // 验证ID字段的特殊配置
//        assertTrue(result.contains("tdText"));
//    }
//
//    @Test
//    void testSqlToFormBasic() throws Exception {
//        String sql = "SELECT id, name, email, status FROM user WHERE id = ?";
//        String formId = "userForm";
//        String datasource = "dataSource";
//
//        String result = parser.sqlToForm(sql, formId, datasource);
//
//        assertNotNull(result);
//        assertTrue(isValidJson(result));
//        assertTrue(result.contains("userForm"));
//        assertTrue(result.contains("id"));
//        assertTrue(result.contains("name"));
//        assertTrue(result.contains("email"));
//        assertTrue(result.contains("status"));
//    }
//
//    @Test
//    void testSqlToFormWithAlias() throws Exception {
//        String sql = "SELECT u.id AS 用户ID, u.name AS 用户姓名, u.email AS 邮箱, u.status AS 状态 FROM user u";
//        String formId = "userFormAlias";
//        String datasource = "dataSource";
//
//        String result = parser.sqlToForm(sql, formId, datasource);
//
//        assertNotNull(result);
//        assertTrue(isValidJson(result));
//        assertTrue(result.contains("用户ID"));
//        assertTrue(result.contains("用户姓名"));
//        assertTrue(result.contains("邮箱"));
//        assertTrue(result.contains("状态"));
//    }
//
//    @Test
//    void testSqlToFormWithDateTimeField() throws Exception {
//        String sql = "SELECT id, name, created_time AS 创建时间, updated_time AS 更新时间 FROM user";
//        String formId = "userFormTime";
//        String datasource = "dataSource";
//
//        String result = parser.sqlToForm(sql, formId, datasource);
//
//        assertNotNull(result);
//        assertTrue(isValidJson(result));
//        assertTrue(result.contains("创建时间"));
//        assertTrue(result.contains("更新时间"));
//        // 时间字段应该配置为日期组件
//        assertTrue(result.contains("date"));
//        assertTrue(result.contains("yyyy-MM-dd HH:mm:ss"));
//    }
//
//    @Test
//    void testSqlToFormWithYesNoField() throws Exception {
//        String sql = "SELECT id, name, is_active AS 是否激活 FROM user";
//        String formId = "userFormYesNo";
//        String datasource = "dataSource";
//
//        String result = parser.sqlToForm(sql, formId, datasource);
//
//        assertNotNull(result);
//        assertTrue(isValidJson(result));
//        assertTrue(result.contains("是否激活"));
//        // 是/否字段应该配置为选择框
//        assertTrue(result.contains("select"));
//    }
//
//    @Test
//    void testSqlToFormErrorOnAllColumns() throws Exception {
//        String sql = "SELECT * FROM user";
//        String formId = "userFormError";
//        String datasource = "dataSource";
//
//        String result = parser.sqlToForm(sql, formId, datasource);
//
//        // 当使用 * 通配符时，应该返回错误
//        assertTrue(result.contains("\"success\":false"));
//        assertTrue(result.contains("请指定字段名及字段中文别名"));
//    }
//
//    @Test
//    void testSqlToFormWithFunction() throws Exception {
//        String sql = "SELECT id, name, COUNT(*) AS 记录总数 FROM user WHERE id = ? GROUP BY id, name";
//        String formId = "userFormFunc";
//        String datasource = "dataSource";
//
//        String result = parser.sqlToForm(sql, formId, datasource);
//
//        assertNotNull(result);
//        assertTrue(isValidJson(result));
//        assertTrue(result.contains("userFormFunc"));
//        assertTrue(result.contains("记录总数"));
//    }
//
//    /**
//     * 验证字符串是否为有效的JSON格式
//     */
//    private boolean isValidJson(String jsonString) {
//        // 简单检查是否是JSON格式
//        jsonString = jsonString.trim();
//        return (jsonString.startsWith("{") && jsonString.endsWith("}"))
//                || (jsonString.startsWith("[") && jsonString.endsWith("]"));
//    }
//}