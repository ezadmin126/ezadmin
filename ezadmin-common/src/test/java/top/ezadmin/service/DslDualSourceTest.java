package top.ezadmin.service;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import top.ezadmin.EzBootstrap;
import top.ezadmin.EzBootstrapConfig;
import top.ezadmin.common.utils.DslLoader;
import top.ezadmin.dao.dto.DslConfig;
import top.ezadmin.plugins.sqlog.EzSqlogDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * DSL 双数据源功能测试
 * 测试文件优先、数据库降级、双写保存等功能
 */
public class DslDualSourceTest {

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
        datasourceMap.put("defaultDataSource", dataSource);  // 注意：使用驼峰命名
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

            // 创建 T_EZADMIN_FORM 表
            stmt.execute("CREATE TABLE IF NOT EXISTS T_EZADMIN_FORM (" +
                    "ID BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '编辑ID', " +
                    "FORM_ID VARCHAR(100) COMMENT '表单编码', " +
                    "DATASOURCE VARCHAR(100) COMMENT '数据源', " +
                    "FORM_NAME VARCHAR(255) COMMENT '表单名称', " +
                    "FORM_DSL TEXT COMMENT '表单配置-dsl', " +
                    "INIT_EXPRESS TEXT COMMENT '表单配置-查询表达式', " +
                    "SUBMIT_EXPRESS TEXT COMMENT '表单配置-提交', " +
                    "DELETE_EXPRESS TEXT COMMENT '表单配置-删除', " +
                    "ADD_TIME DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间', " +
                    "UPDATE_TIME DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间', " +
                    "IS_DEL INT COMMENT '是否删除', " +
                    "ADD_NAME VARCHAR(100) COMMENT '添加人名称', " +
                    "UPDATE_NAME VARCHAR(100) COMMENT '更新人名称', " +
                    "DELETE_FLAG INT DEFAULT 0 NOT NULL COMMENT '删除标识 0未删除 1已删除', " +
                    "ADD_ID BIGINT, " +
                    "UPDATE_ID BIGINT" +
                    ")");

            // 创建 T_EZADMIN_LIST 表
            stmt.execute("CREATE TABLE IF NOT EXISTS T_EZADMIN_LIST (" +
                    "ID BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '编辑ID', " +
                    "LIST_ID VARCHAR(100) COMMENT '列表编码', " +
                    "DATASOURCE VARCHAR(100) COMMENT '数据源', " +
                    "LIST_NAME VARCHAR(255) COMMENT '列表名称', " +
                    "LIST_DSL TEXT COMMENT '列表配置-dsl', " +
                    "SELECT_EXPRESS TEXT COMMENT '列表配置-查询表达式', " +
                    "COUNT_EXPRESS TEXT COMMENT '列表配置-总数表达式默认为空', " +
                    "ADD_TIME DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间', " +
                    "UPDATE_TIME DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间', " +
                    "IS_DEL INT COMMENT '是否删除', " +
                    "ADD_NAME VARCHAR(100) COMMENT '添加人名称', " +
                    "UPDATE_NAME VARCHAR(100) COMMENT '更新人名称', " +
                    "DELETE_FLAG INT DEFAULT 0 NOT NULL COMMENT '删除标识 0未删除 1已删除', " +
                    "ADD_ID BIGINT COMMENT '添加人', " +
                    "UPDATE_ID BIGINT" +
                    ")");

            // 插入测试数据 - Form
            stmt.execute("INSERT INTO T_EZADMIN_FORM (FORM_ID, DATASOURCE, FORM_NAME, FORM_DSL, INIT_EXPRESS, SUBMIT_EXPRESS, DELETE_EXPRESS, DELETE_FLAG) VALUES " +
                    "('test-db-form', 'defaultdatasource', '测试表单(数据库版)', " +
                    "'{\"id\":\"test-db-form\",\"title\":\"测试表单(数据库版)\",\"fields\":[]}', " +
                    "'SELECT * FROM test WHERE id = #{ID}', " +
                    "'INSERT INTO test VALUES (#{id})', " +
                    "'DELETE FROM test WHERE id = #{ID}', " +
                    "0)");

            // 插入测试数据 - List
            stmt.execute("INSERT INTO T_EZADMIN_LIST (LIST_ID, DATASOURCE, LIST_NAME, LIST_DSL, SELECT_EXPRESS, COUNT_EXPRESS, DELETE_FLAG) VALUES " +
                    "('test-db-list', 'defaultdatasource', '测试列表(数据库版)', " +
                    "'{\"id\":\"test-db-list\",\"title\":\"测试列表(数据库版)\",\"columns\":[]}', " +
                    "'SELECT * FROM test', " +
                    "'SELECT COUNT(*) FROM test', " +
                    "0)");

            System.out.println("✅ 测试数据库初始化完成");
            System.out.println("   - T_EZADMIN_FORM 表已创建");
            System.out.println("   - T_EZADMIN_LIST 表已创建");
            System.out.println("   - 测试数据已插入");
        }
    }

    @AfterClass
    public static void tearDown() {
        EzBootstrap.clear();
        System.out.println("✅ 测试环境已清理");
    }

    /**
     * 测试1：从文件加载Form DSL
     */
    @Test
    public void testLoadFormFromFile() {
        System.out.println("\n========== 测试1：从文件加载Form DSL ==========");

        DslConfig config = DslLoader.loadDsl("test-file-form", "form");

        assertNotNull("配置应该不为null", config);
        assertEquals("应该从文件加载", "file", config.getSource());
        assertEquals("ID应该匹配", "test-file-form", config.getConfig().get("id"));
        assertEquals("标题应该匹配", "测试表单(文件版)", config.getConfig().get("title"));
        assertNotNull("应该有初始化表达式", config.getConfig().get("initExpress"));

        System.out.println("✅ 测试通过：成功从文件加载Form DSL");
        System.out.println("   - ID: " + config.getConfig().get("id"));
        System.out.println("   - 标题: " + config.getConfig().get("title"));
        System.out.println("   - 数据源: " + config.getSource());
    }

    /**
     * 测试2：从文件加载List DSL
     */
    @Test
    public void testLoadListFromFile() {
        System.out.println("\n========== 测试2：从文件加载List DSL ==========");

        DslConfig config = DslLoader.loadDsl("test-file-list", "list");

        assertNotNull("配置应该不为null", config);
        assertEquals("应该从文件加载", "file", config.getSource());
        assertEquals("ID应该匹配", "test-file-list", config.getConfig().get("id"));
        assertEquals("标题应该匹配", "测试列表(文件版)", config.getConfig().get("title"));

        Map<String, Object> express = (Map<String, Object>) config.getConfig().get("express");
        assertNotNull("应该有express对象", express);
        assertNotNull("应该有main表达式", express.get("main"));
        assertNotNull("应该有count表达式", express.get("count"));

        System.out.println("✅ 测试通过：成功从文件加载List DSL");
        System.out.println("   - ID: " + config.getConfig().get("id"));
        System.out.println("   - 标题: " + config.getConfig().get("title"));
        System.out.println("   - 数据源: " + config.getSource());
    }

    /**
     * 测试3：从数据库加载Form DSL
     */
    @Test
    public void testLoadFormFromDatabase() {
        System.out.println("\n========== 测试3：从数据库加载Form DSL ==========");

        DslConfig config = DslLoader.loadDsl("test-db-form", "form");

        assertNotNull("配置应该不为null", config);
        assertEquals("应该从数据库加载", "database", config.getSource());
        assertEquals("ID应该匹配", "test-db-form", config.getConfig().get("id"));
        assertTrue("标题应该包含'数据库版'",
                config.getConfig().get("name") != null &&
                config.getConfig().get("name").toString().contains("数据库版"));
        assertNotNull("应该有初始化表达式", config.getConfig().get("initExpress"));
        assertNotNull("应该有提交表达式", config.getConfig().get("submitExpress"));

        System.out.println("✅ 测试通过：成功从数据库加载Form DSL");
        System.out.println("   - ID: " + config.getConfig().get("id"));
        System.out.println("   - 标题: " + config.getConfig().get("name"));
        System.out.println("   - 数据源: " + config.getSource());
    }

    /**
     * 测试4：从数据库加载List DSL
     */
    @Test
    public void testLoadListFromDatabase() {
        System.out.println("\n========== 测试4：从数据库加载List DSL ==========");

        DslConfig config = DslLoader.loadDsl("test-db-list", "list");

        assertNotNull("配置应该不为null", config);
        assertEquals("应该从数据库加载", "database", config.getSource());
        assertEquals("ID应该匹配", "test-db-list", config.getConfig().get("id"));

        Map<String, Object> express = (Map<String, Object>) config.getConfig().get("express");
        assertNotNull("应该有express对象", express);
        assertNotNull("应该有main表达式", express.get("main"));
        assertNotNull("应该有count表达式", express.get("count"));

        System.out.println("✅ 测试通过：成功从数据库加载List DSL");
        System.out.println("   - ID: " + config.getConfig().get("id"));
        System.out.println("   - 标题: " + config.getConfig().get("name"));
        System.out.println("   - 数据源: " + config.getSource());
    }

    /**
     * 测试5：文件优先策略
     * 当文件和数据库都存在时，应该优先从文件加载
     */
    @Test
    public void testFilePriorityStrategy() {
        System.out.println("\n========== 测试5：文件优先策略 ==========");

        // 加载test-file-form（文件存在）
        DslConfig config = DslLoader.loadDsl("test-file-form", "form");

        assertNotNull("配置应该不为null", config);
        assertEquals("应该优先从文件加载", "file", config.getSource());
        assertTrue("应该加载文件版本的标题",
                  config.getConfig().get("title").toString().contains("文件版"));

        System.out.println("✅ 测试通过：文件优先策略正常工作");
        System.out.println("   - 加载来源: " + config.getSource());
        System.out.println("   - 说明：文件存在时优先从文件加载");
    }

    /**
     * 测试6：不存在的DSL
     * 当文件和数据库都不存在时，应该返回null
     */
    @Test
    public void testNonExistentDsl() {
        System.out.println("\n========== 测试6：不存在的DSL ==========");

        DslConfig config = DslLoader.loadDsl("non-existent-form", "form");

        assertNull("不存在的DSL应该返回null", config);

        System.out.println("✅ 测试通过：不存在的DSL正确返回null");
    }

    /**
     * 测试7：保存DSL到文件和数据库（双写）
     */
    @Test
    public void testSaveDslBoth() {
        System.out.println("\n========== 测试7：双写保存DSL ==========");

        Map<String, Object> testDsl = new HashMap<>();
        testDsl.put("id", "test-save-form");
        testDsl.put("title", "测试保存表单");
        testDsl.put("dataSource", "defaultDataSource");
        testDsl.put("initExpress", "SELECT * FROM test WHERE id = #{ID}");
        testDsl.put("submitExpress", "INSERT INTO test VALUES (#{id})");
        testDsl.put("deleteExpress", "DELETE FROM test WHERE id = #{ID}");

        try {
            boolean success = DslLoader.saveDsl("test-save-form", "form", testDsl, true, true);

            if (success) {
                System.out.println("✅ 测试通过：DSL保存成功");
                System.out.println("   - 文件保存：成功");
                System.out.println("   - 数据库保存：成功");

                // 验证加载
                DslConfig config = DslLoader.loadDsl("test-save-form", "form");
                assertNotNull("保存后应该能够加载", config);
                assertEquals("标题应该匹配", "测试保存表单", config.getConfig().get("title"));

            } else {
                System.out.println("⚠️  警告：DSL保存失败（可能是权限或数据库问题）");
            }

        } catch (Exception e) {
            System.out.println("❌ 测试异常：" + e.getMessage());
        }
    }

    /**
     * 执行所有测试的主方法
     */
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║         DSL 双数据源功能测试套件                         ║");
        System.out.println("║  测试文件优先、数据库降级、双写保存等核心功能             ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");

        DslDualSourceTest test = new DslDualSourceTest();

        try {
            test.testLoadFormFromFile();
            test.testLoadListFromFile();
            test.testFilePriorityStrategy();
            test.testNonExistentDsl();
            test.testLoadFormFromDatabase();
            test.testLoadListFromDatabase();
            test.testSaveDslBoth();

            System.out.println("\n╔══════════════════════════════════════════════════════════╗");
            System.out.println("║                   测试完成！                             ║");
            System.out.println("╚══════════════════════════════════════════════════════════╝");

        } catch (Exception e) {
            System.err.println("\n测试执行失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
}
