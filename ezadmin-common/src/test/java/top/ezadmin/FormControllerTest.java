package top.ezadmin;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.controller.FormController;
import top.ezadmin.plugins.sqlog.EzSqlogDataSource;
import top.ezadmin.web.EzResult;
import top.ezadmin.web.RequestContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * 表单功能单元测试
 * 测试 FormController 的各种方法，无需 Servlet 依赖
 */
public class FormControllerTest {

    private static DataSource dataSource;
    private static FormController formController;

    @BeforeClass
    public static void setup() throws Exception {
        // 1. 创建 H2 内存数据库
        JdbcDataSource h2DataSource = new JdbcDataSource();
        h2DataSource.setURL("jdbc:h2:mem:testdb_form;DB_CLOSE_DELAY=-1;MODE=MySQL");
        h2DataSource.setUser("sa");
        h2DataSource.setPassword("");
        EzSqlogDataSource ezSqlogDataSource = new EzSqlogDataSource();
        ezSqlogDataSource.setRealDataSource(h2DataSource);
        ezSqlogDataSource.setLogType("1000-10000");
        dataSource = ezSqlogDataSource;

        // 2. 初始化表结构和测试数据
        initTestData();

        // 3. 配置 EzBootstrap
        EzBootstrapConfig config = new EzBootstrapConfig();
        Map<String, DataSource> datasourceMap = new HashMap<>();
        datasourceMap.put("defaultdatasource", dataSource);
        datasourceMap.put("datasource", dataSource);
        config.setDatasourceMap(datasourceMap);

        // 设置空的插件配置列表，避免 NPE
        config.setPluginsFormConfigResources(new java.util.ArrayList<>());
        config.setPluginsListConfigResources(new java.util.ArrayList<>());
        config.setListConfigResources(new java.util.ArrayList<>());
        config.setFormConfigResources(new java.util.ArrayList<>());

        EzBootstrap.getInstance().init(config);

        // 4. 初始化 FormController
        formController = new FormController();
    }

    private static void initTestData() throws Exception {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // 创建 students 表
            stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                    "students_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "username VARCHAR(100), " +
                    "useremail VARCHAR(100), " +
                    "usersex INT, " +
                    "area_id INT, " +
                    "add_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "ruxue_date DATE, " +
                    "delete_flag INT DEFAULT 0)");

            // 创建 hobbys 表
            stmt.execute("CREATE TABLE IF NOT EXISTS hobbys (" +
                    "hobbys_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(50))");

            // 创建关联表
            stmt.execute("CREATE TABLE IF NOT EXISTS rel_students_hobbys (" +
                    "students_id INT, " +
                    "hobbys_id INT)");

            // 创建 regions 表
            stmt.execute("CREATE TABLE IF NOT EXISTS regions (" +
                    "region_id INT PRIMARY KEY, " +
                    "name VARCHAR(100), " +
                    "parent_id INT)");

            // 插入测试数据
            stmt.execute("INSERT INTO hobbys (hobbys_id, name) VALUES (1, '篮球'), (2, '足球'), (3, '游泳')");

            stmt.execute("INSERT INTO regions (region_id, name, parent_id) VALUES " +
                    "(1, '中国', 0), (2, '北京', 1), (3, '上海', 1), (4, '朝阳区', 2)");

            stmt.execute("INSERT INTO students (students_id, username, useremail, usersex, area_id, ruxue_date, delete_flag) VALUES " +
                    "(1, '张三', 'zhangsan@test.com', 1, 2, '2024-09-01', 0), " +
                    "(2, '李四', 'lisi@test.com', 0, 3, '2024-09-01', 0), " +
                    "(3, '王五', 'wangwu@test.com', 1, 4, '2024-09-01', 0)");

            stmt.execute("INSERT INTO rel_students_hobbys (students_id, hobbys_id) VALUES " +
                    "(1, 1), (1, 2), (2, 2), (2, 3), (3, 1)");
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (dataSource != null && dataSource instanceof EzSqlogDataSource) {
            Connection conn = dataSource.getConnection();
            if (conn != null) {
                conn.close();
            }
        }
    }

    /**
     * 创建测试用的 RequestContext
     */
    private RequestContext createRequestContext(Map<String, Object> params) {
        RequestContext context = new RequestContext();
        Map<String, Object> requestParams = new HashMap<>();
        if (params != null) {
            requestParams.putAll(params);
        }
        context.setRequestParams(requestParams);

        Map<String, String> sessionParams = new HashMap<>();
        sessionParams.put("EZ_SESSION_USER_ID_KEY", "testUser");
        sessionParams.put("EZ_SESSION_USER_NAME_KEY", "测试用户");
        context.setSessionParams(sessionParams);

        context.setContextPath("");
        context.setServerName("localhost");
        context.setServerPort(8080);

        return context;
    }

    /**
     * 测试 page 方法 - 渲染表单页面
     */
    @Test
    public void testFormPage() throws Exception {
        System.out.println("=== testFormPage ===");

        Map<String, Object> params = new HashMap<>();
        RequestContext context = createRequestContext(params);

        EzResult result = formController.page(context, "page", "test");

        assertNotNull("Result should not be null", result);
        assertNotNull("Template data should not be null", result.getData());

        System.out.println("Page result code: " + result.getCode());
        System.out.println("Page rendered successfully");
    }

    /**
     * 测试 data 方法 - 获取表单初始化数据（不带ID）
     */
    @Test
    public void testFormDataWithoutId() throws Exception {
        System.out.println("=== testFormDataWithoutId ===");

        Map<String, Object> params = new HashMap<>();
        RequestContext context = createRequestContext(params);

        EzResult result = formController.data(context, "data", "test");

        assertNotNull("Result should not be null", result);
        assertEquals("Code should be JSON", "JSON", result.getCode());

        EzResult innerResult = (EzResult) result.getData();
        assertNotNull("Inner result should not be null", innerResult);

        Map<String, Object> data = (Map<String, Object>) innerResult.getData();
        System.out.println("Data (no ID): " + JSONUtils.toJSONString(data));
    }

    /**
     * 测试 data 方法 - 获取表单初始化数据（带ID）
     */
    @Test
    public void testFormDataWithId() throws Exception {
        System.out.println("=== testFormDataWithId ===");

        Map<String, Object> params = new HashMap<>();
        params.put("ID", "1");
        RequestContext context = createRequestContext(params);

        EzResult result = formController.data(context, "data", "test");

        assertNotNull("Result should not be null", result);
        assertEquals("Code should be JSON", "JSON", result.getCode());

        EzResult innerResult = (EzResult) result.getData();
        assertNotNull("Inner result should not be null", innerResult);

        Map<String, Object> data = (Map<String, Object>) innerResult.getData();
        assertNotNull("Data should not be null", data);

        System.out.println("Data (ID=1): " + JSONUtils.toJSONString(data));

        // 验证返回的数据（注意：字段名是大写的）
        assertEquals("Username should match", "张三", data.get("USER_NAME"));
        assertEquals("Email should match", "zhangsan@test.com", data.get("USER_EMAIL"));
        assertEquals("Sex should match", "1", data.get("USER_SEX"));

        System.out.println("Data (ID=1): " + JSONUtils.toJSONString(data));
    }

    /**
     * 测试 submit 方法 - 新增学生
     */
    @Test
    public void testFormSubmitCreate() throws Exception {
        System.out.println("=== testFormSubmitCreate ===");

        Map<String, Object> params = new HashMap<>();
        params.put("username", "赵六");
        params.put("useremail", "zhaoliu@test.com");
        params.put("usersex", "1");
        params.put("area_id", "2");
        params.put("ruxue_date", "2024-09-01");
        params.put("hobbys_ids", "1,3");

        RequestContext context = createRequestContext(params);

        EzResult result = formController.submit(context, "submit", "test");

        assertNotNull("Result should not be null", result);
        assertTrue("Submit should be successful", result.isSuccess());

        Object newId = result.getData();
        assertNotNull("New ID should not be null", newId);

        System.out.println("New student created with ID: " + newId);

        // 验证数据是否插入成功
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("ID", String.valueOf(newId).split("=")[1]);
        RequestContext queryContext = createRequestContext(queryParams);

        EzResult dataResult = formController.data(queryContext, "data", "test");
        EzResult innerResult = (EzResult) dataResult.getData();
        Map<String, Object> data = (Map<String, Object>) innerResult.getData();

        assertEquals("Username should match", "赵六", data.get("USER_NAME"));
        System.out.println("Verified created student: " + JSONUtils.toJSONString(data));
    }

    /**
     * 测试 submit 方法 - 更新学生
     */
    @Test
    public void testFormSubmitUpdate() throws Exception {
        System.out.println("=== testFormSubmitUpdate ===");

        Map<String, Object> params = new HashMap<>();
        params.put("students_id", "88");
        params.put("username", "李四更新");
        params.put("useremail", "lisi_updated@test.com");
        params.put("usersex", "0");
        params.put("area_id", "3");
        params.put("ruxue_date", "2024-09-01");
        params.put("hobbys_ids", "2");

        RequestContext context = createRequestContext(params);

        EzResult result = formController.submit(context, "submit", "test");

        assertNotNull("Result should not be null", result);
        assertTrue("Submit should be successful", result.isSuccess());

        System.out.println("Student updated: " + result.getData());

        // 验证数据是否更新成功
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("ID", "88");
        RequestContext queryContext = createRequestContext(queryParams);

        EzResult dataResult = formController.data(queryContext, "data", "test");
        EzResult innerResult = (EzResult) dataResult.getData();
        Map<String, Object> data = (Map<String, Object>) innerResult.getData();

        assertEquals("Username should be updated", "李四更新", data.get("USER_NAME"));
        assertEquals("Email should be updated", "lisi_updated@test.com", data.get("USER_EMAIL"));
        System.out.println("Verified updated student: " + JSONUtils.toJSONString(data));
    }

    /**
     * 测试 delete 方法 - 删除学生（软删除）
     */
    @Test
    public void testFormDelete() throws Exception {
        System.out.println("=== testFormDelete ===");

        Map<String, Object> params = new HashMap<>();
        params.put("ID", "3");
        RequestContext context = createRequestContext(params);

        EzResult result = formController.delete(context, "delete", "test");

        assertNotNull("Result should not be null", result);
        assertTrue("Delete should be successful", result.isSuccess());

        System.out.println("Student deleted: " + result.getData());

        // 验证数据是否被软删除
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            java.sql.ResultSet rs = stmt.executeQuery(
                    "SELECT delete_flag FROM students WHERE students_id = 3"
            );
            if (rs.next()) {
                int deleteFlag = rs.getInt("delete_flag");
                assertEquals("Delete flag should be 1", 1, deleteFlag);
                System.out.println("Verified: student marked as deleted (delete_flag=1)");
            }
        }
    }

    /**
     * 测试 status 方法 - 更新学生状态
     */
    @Test
    public void testFormStatus() throws Exception {
        System.out.println("=== testFormStatus ===");

        Map<String, Object> params = new HashMap<>();
        params.put("ID", "1");
        params.put("delete_flag", "1");
        RequestContext context = createRequestContext(params);

        EzResult result = formController.status(context, "status", "test");

        assertNotNull("Result should not be null", result);
        assertTrue("Status update should be successful", result.isSuccess());

        System.out.println("Student status updated: " + result.getData());

        // 验证状态是否更新成功
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            java.sql.ResultSet rs = stmt.executeQuery(
                    "SELECT delete_flag FROM students WHERE students_id = 1"
            );
            if (rs.next()) {
                int deleteFlag = rs.getInt("delete_flag");
                assertEquals("Delete flag should be 1", 1, deleteFlag);
                System.out.println("Verified: status updated (delete_flag=1)");
            }
        }
    }

    /**
     * 测试 page 方法 - 404 情况（表单不存在）
     */
    @Test
    public void testFormPageNotFound() throws Exception {
        System.out.println("=== testFormPageNotFound ===");

        Map<String, Object> params = new HashMap<>();
        RequestContext context = createRequestContext(params);

        EzResult result = formController.page(context, "page", "non_existent_form");

        assertNotNull("Result should not be null", result);
        assertEquals("Code should be 404", "404", result.getCode());

        System.out.println("404 test passed");
    }
}
