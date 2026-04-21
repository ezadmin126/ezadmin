package top.ezadmin;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.controller.ListController;
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
 * 列表功能单元测试
 * 测试 ListController 的各种方法，无需 Servlet 依赖
 */
public class ListControllerTest {

    private static DataSource dataSource;

    @BeforeClass
    public static void setup() throws Exception {
        // 1. 创建 H2 内存数据库
        JdbcDataSource h2DataSource = new JdbcDataSource();
        h2DataSource.setURL("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL");
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
    public static void tearDown() {
        EzBootstrap.clear();
    }

    /**
     * 测试 DSL 列表页面渲染
     */
    @Test
    public void testDslListPage() throws Exception {
        RequestContext ctx = createRequestContext();
        ctx.setRequestURI("/topezadmin/list/page-test");

        EzResult result = EzBootstrap.getInstance().generate(ctx);

        assertNotNull("Result should not be null", result);
        System.out.println("=== testDslListPage ===");
        System.out.println("Code: " + result.getCode());
        // DSL 页面返回 HTML 模板
        assertNotNull("Data should not be null", result.getData());
    }

    /**
     * 测试 DSL 列表数据接口
     */
    @Test
    public void testDslListData() throws Exception {
        RequestContext ctx = createRequestContext();
        ctx.setRequestURI("/topezadmin/list/data-test");

        Map<String, Object> params = ctx.getRequestParams();
        params.put("page", 1);
        params.put("perPageInt", 10);

        EzResult result = EzBootstrap.getInstance().generate(ctx);

        assertNotNull("Result should not be null", result);
        assertEquals("Code should be JSON", "JSON", result.getCode());
        System.out.println("=== testDslListData ===");
        System.out.println("Result: " + JSONUtils.toJSONString(result.getData()));
    }

    /**
     * 测试带搜索条件的列表数据
     */
    @Test
    public void testDslListDataWithSearch() throws Exception {
        RequestContext ctx = createRequestContext();
        ctx.setRequestURI("/topezadmin/list/data-test");

        Map<String, Object> params = ctx.getRequestParams();
        params.put("page", 1);
        params.put("perPageInt", 10);
        params.put("USER_NAME", "张");  // 模糊搜索

        EzResult result = EzBootstrap.getInstance().generate(ctx);

        assertNotNull("Result should not be null", result);
        assertEquals("Code should be JSON", "JSON", result.getCode());
        System.out.println("=== testDslListDataWithSearch ===");
        System.out.println("Search Result: " + result.getData());
    }

    /**
     * 测试直接调用 ListController.dsl 方法
     */
    @Test
    public void testDirectControllerDsl() throws Exception {
        ListController controller = new ListController();
        RequestContext ctx = createRequestContext();

        EzResult result = controller.page(ctx, "page", "test");

        assertNotNull("Result should not be null", result);
        System.out.println("=== testDirectControllerDsl ===");
        System.out.println("Template generated successfully");
    }

    /**
     * 测试直接调用 ListController.data 方法
     */
    @Test
    public void testDirectControllerData() throws Exception {
        ListController controller = new ListController();
        RequestContext ctx = createRequestContext();

        Map<String, Object> params = ctx.getRequestParams();
        params.put("page", 1);
        params.put("perPageInt", 5);

        EzResult result = controller.data(ctx, "data", "test");

        assertNotNull("Result should not be null", result);
        assertEquals("Code should be JSON", "JSON", result.getCode());
        System.out.println("=== testDirectControllerData ===");
        System.out.println("Data: " + JSONUtils.toJSONString(result.getData()));
        System.out.println("Count: " + result.getCount());
        assertTrue("Count should be greater than 0", result.getCount() > 0);
    }

    /**
     * 测试分页功能
     */
    @Test
    public void testPagination() throws Exception {
        ListController controller = new ListController();
        RequestContext ctx = createRequestContext();

        Map<String, Object> params = ctx.getRequestParams();
        params.put("page", 1);
        params.put("perPageInt", 2);  // 每页2条

        EzResult result = controller.data(ctx, "data", "test");

        assertNotNull("Result should not be null", result);
        System.out.println("=== testPagination ===");
        System.out.println("Page 1 Data: " + result.getData());
        System.out.println("Total Count: " + result.getCount());
    }

    /**
     * 测试排序功能
     */
    @Test
    public void testOrderBy() throws Exception {
        ListController controller = new ListController();
        RequestContext ctx = createRequestContext();

        Map<String, Object> params = ctx.getRequestParams();
        params.put("page", 1);
        params.put("perPageInt", 10);
        params.put("orderByName", "USER_NAME");
        params.put("orderByType", "desc");

        EzResult result = controller.data(ctx, "data", "test");

        assertNotNull("Result should not be null", result);
        System.out.println("=== testOrderBy ===");
        System.out.println("Ordered Data: " + JSONUtils.toJSONString(result.getData()));
    }

    /**
     * 测试下拉选择数据查询
     */
    @Test
    public void testSelectInitData() throws Exception {
        RequestContext ctx = createRequestContext();
        ctx.setRequestURI("/topezadmin/list/page-test");

        EzResult result = EzBootstrap.getInstance().generate(ctx);

        assertNotNull("Result should not be null", result);
        System.out.println("=== testSelectInitData ===");
        // 页面渲染时会加载 select 的初始数据
        System.out.println("Page with select init data generated");
    }

    /**
     * 测试会话参数传递
     */
    @Test
    public void testSessionParams() throws Exception {
        ListController controller = new ListController();
        RequestContext ctx = createRequestContext();

        // 设置会话参数
        Map<String, String> sessionParams = new HashMap<>();
        sessionParams.put("EZ_SESSION_USER_ID_KEY", "100");
        sessionParams.put("EZ_SESSION_USER_NAME_KEY", "testUser");
        ctx.setSessionParams(sessionParams);

        Map<String, Object> params = ctx.getRequestParams();
        params.put("page", 1);
        params.put("perPageInt", 10);

        EzResult result = controller.data(ctx, "data", "test");

        assertNotNull("Result should not be null", result);
        System.out.println("=== testSessionParams ===");
        System.out.println("Result with session: " + result.getData());
    }

    /**
     * 测试日期范围搜索
     */
    @Test
    public void testDateRangeSearch() throws Exception {
        ListController controller = new ListController();
        RequestContext ctx = createRequestContext();

        Map<String, Object> params = ctx.getRequestParams();
        params.put("page", 1);
        params.put("perPageInt", 10);
        params.put("ADD_TIME", "2024-01-01 - 2025-12-31");

        EzResult result = controller.data(ctx, "data", "test");

        assertNotNull("Result should not be null", result);
        System.out.println("=== testDateRangeSearch ===");
        System.out.println("Date range search result: " + result.getData());
    }

    /**
     * 测试多选搜索 (IN 查询)
     */
    @Test
    public void testMultiSelectSearch() throws Exception {
        ListController controller = new ListController();
        RequestContext ctx = createRequestContext();

        Map<String, Object> params = ctx.getRequestParams();
        params.put("page", 1);
        params.put("perPageInt", 10);
        params.put("HOBBYS_IDS", "1,2");  // 多选爱好

        EzResult result = controller.data(ctx, "data", "test");

        assertNotNull("Result should not be null", result);
        System.out.println("=== testMultiSelectSearch ===");
        System.out.println("Multi-select search result: " + result.getData());
    }

    @Test
    public void testJDBCTYPE() throws Exception {
        ListController controller = new ListController();
        RequestContext ctx = createRequestContext();

        Map<String, Object> params = ctx.getRequestParams();
        params.put("page", 1);
        params.put("perPageInt", 10);
        params.put("USER_SEX", "1");  // 多选爱好

        EzResult result = controller.data(ctx, "data", "test");

        assertNotNull("Result should not be null", result);
        System.out.println("=== testMultiSelectSearch ===");
        System.out.println("Multi-select search result: " + result.getData());
    }

    /**
     * 测试不存在的列表ID
     */
    @Test
    public void testNotExistList() {
        RequestContext ctx = createRequestContext();
        ctx.setRequestURI("/topezadmin/list/page-notexist");

        EzResult result = EzBootstrap.getInstance().generate(ctx);

        System.out.println("=== testNotExistList ===");
        System.out.println("Result for non-exist list: " + result.getCode());
    }

    /**
     * 创建基础的 RequestContext
     */
    private RequestContext createRequestContext() {
        RequestContext ctx = new RequestContext();
        ctx.setContextPath("");
        ctx.setServerName("localhost");
        ctx.setServerPort(8080);
        ctx.setMethod("GET");
        ctx.setRequestParams(new HashMap<>());
        ctx.setSessionParams(new HashMap<>());
        return ctx;
    }
}
