package top.ezadmin;

import org.junit.Test;
import top.ezadmin.common.utils.ConfigFileLoader;
import top.ezadmin.common.utils.ExpressFileLoader;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * 表达式文件引用功能测试
 */
public class ExpressFileLoaderTest {

    /**
     * 测试列表配置的表达式文件引用
     */
    @Test
    public void testListExpressFileReference() throws Exception {
        System.out.println("===== 测试列表配置的表达式文件引用 =====");

        // 设置为开发模式（启用热加载）
        EzBootstrap.config().setSqlCache(false);

        // 加载配置文件
        String configPath = "topezadmin/config/layui/dsl/list/test-express-ref.json";
        Map<String, Object> config = ConfigFileLoader.loadConfigFile(configPath);

        assertNotNull("配置文件应该成功加载", config);
        System.out.println("配置文件加载成功");

        // 获取 express 节点
        @SuppressWarnings("unchecked")
        Map<String, Object> express = (Map<String, Object>) config.get("express");
        assertNotNull("express 节点应该存在", express);

        // 处理前的值（应该是文件引用）
        String mainBefore = (String) express.get("main");
        String countBefore = (String) express.get("count");
        String groupBefore = (String) express.get("groupBy");

        System.out.println("处理前:");
        System.out.println("  main: " + mainBefore);
        System.out.println("  count: " + countBefore);
        System.out.println("  groupBy: " + groupBefore);

        assertTrue("main 应该以 $$ 开头", mainBefore.startsWith("$$"));
        assertTrue("count 应该以 $$ 开头", countBefore.startsWith("$$"));
        assertTrue("groupBy 应该以 $$ 开头", groupBefore.startsWith("$$"));

        // 处理表达式引用
        ExpressFileLoader.processExpressReferences(config, configPath);

        // 处理后的值（应该是文件内容）
        String mainAfter = (String) express.get("main");
        String countAfter = (String) express.get("count");
        String groupAfter = (String) express.get("groupBy");
        String orderBy = (String) express.get("orderBy");

        System.out.println("\n处理后:");
        System.out.println("  main: " + (mainAfter.length() > 50 ? mainAfter.substring(0, 50) + "..." : mainAfter));
        System.out.println("  count: " + (countAfter.length() > 50 ? countAfter.substring(0, 50) + "..." : countAfter));
        System.out.println("  groupBy: " + groupAfter);
        System.out.println("  orderBy: " + orderBy);

        // 验证文件内容已经被加载
        assertFalse("main 不应该再以 $$ 开头", mainAfter.startsWith("$$"));
        assertFalse("count 不应该再以 $$ 开头", countAfter.startsWith("$$"));
        assertFalse("groupBy 不应该再以 $$ 开头", groupAfter.startsWith("$$"));

        assertTrue("main 应该包含 SELECT", mainAfter.contains("SELECT"));
        assertTrue("count 应该包含 COUNT", countAfter.contains("COUNT"));
        assertEquals("orderBy 应该保持不变", "create_time DESC", orderBy);

        System.out.println("\n✅ 列表配置的表达式文件引用测试通过");
    }

    /**
     * 测试表单配置的表达式文件引用
     */
    @Test
    public void testFormExpressFileReference() throws Exception {
        System.out.println("===== 测试表单配置的表达式文件引用 =====");

        // 设置为开发模式（启用热加载）
        EzBootstrap.config().setSqlCache(false);

        // 加载配置文件
        String configPath = "topezadmin/config/layui/dsl/form/test-form-express.json";
        Map<String, Object> config = ConfigFileLoader.loadConfigFile(configPath);

        assertNotNull("配置文件应该成功加载", config);
        System.out.println("配置文件加载成功");

        // 获取 express 节点
        @SuppressWarnings("unchecked")
        Map<String, Object> express = (Map<String, Object>) config.get("express");
        assertNotNull("express 节点应该存在", express);

        // 处理前的值
        String initBefore = (String) express.get("init");
        String submitBefore = (String) express.get("submit");
        String deleteBefore = (String) express.get("delete");

        System.out.println("处理前:");
        System.out.println("  init: " + initBefore);
        System.out.println("  submit: " + submitBefore);
        System.out.println("  delete: " + deleteBefore);

        assertTrue("init 应该以 $$ 开头", initBefore.startsWith("$$"));
        assertTrue("submit 应该以 $$ 开头", submitBefore.startsWith("$$"));
        assertTrue("delete 应该以 $$ 开头", deleteBefore.startsWith("$$"));

        // 处理表达式引用
        ExpressFileLoader.processExpressReferences(config, configPath);

        // 处理后的值
        String initAfter = (String) express.get("init");
        String submitAfter = (String) express.get("submit");
        String deleteAfter = (String) express.get("delete");

        System.out.println("\n处理后:");
        System.out.println("  init: " + initAfter);
        System.out.println("  submit 长度: " + submitAfter.length() + " 字符");
        System.out.println("  delete 长度: " + deleteAfter.length() + " 字符");

        // 验证文件内容已经被加载
        assertFalse("init 不应该再以 $$ 开头", initAfter.startsWith("$$"));
        assertFalse("submit 不应该再以 $$ 开头", submitAfter.startsWith("$$"));
        assertFalse("delete 不应该再以 $$ 开头", deleteAfter.startsWith("$$"));

        assertTrue("init 应该包含初始化逻辑", initAfter.contains("setValue"));
        assertTrue("submit 应该包含提交逻辑", submitAfter.contains("UPDATE") || submitAfter.contains("INSERT"));
        assertTrue("delete 应该包含删除逻辑", deleteAfter.contains("delete_flag"));

        System.out.println("\n✅ 表单配置的表达式文件引用测试通过");
    }

    /**
     * 测试生产模式（使用 classpath）
     */
    @Test
    public void testProductionMode() throws Exception {
        System.out.println("===== 测试生产模式（classpath）=====");

        // 设置为生产模式（禁用热加载）
        EzBootstrap.config().setSqlCache(true);

        // 加载配置文件
        String configPath = "topezadmin/config/layui/dsl/list/test-express-ref.json";
        Map<String, Object> config = ConfigFileLoader.loadConfigFile(configPath);

        assertNotNull("配置文件应该成功加载", config);

        // 获取 express 节点
        @SuppressWarnings("unchecked")
        Map<String, Object> express = (Map<String, Object>) config.get("express");

        // 处理表达式引用
        ExpressFileLoader.processExpressReferences(config, configPath);

        // 验证文件内容已经被加载
        String mainAfter = (String) express.get("main");
        assertFalse("main 不应该再以 $$ 开头", mainAfter.startsWith("$$"));
        assertTrue("main 应该包含 SELECT", mainAfter.contains("SELECT"));

        System.out.println("生产模式下也能正确加载表达式文件");
        System.out.println("✅ 生产模式测试通过");
    }

    /**
     * 测试表达式文件的热加载
     */
    @Test
    public void testExpressFileHotReload() throws Exception {
        System.out.println("===== 测试表达式文件的热加载 =====");

        // 设置为开发模式
        EzBootstrap.config().setSqlCache(false);

        String currentDir = System.getProperty("user.dir");
        String expressFilePath = currentDir + "/src/main/resources/topezadmin/config/layui/dsl/list/test_hot_reload.txt";

        try {
            // 创建初始表达式文件
            String initialContent = "SELECT * FROM test_table WHERE status = 1";
            Files.write(Paths.get(expressFilePath), initialContent.getBytes(StandardCharsets.UTF_8));
            System.out.println("创建初始表达式文件: " + expressFilePath);

            // 创建引用该文件的配置
            String configPath = currentDir + "/src/main/resources/topezadmin/config/layui/dsl/list/test_hot_config.json";
            String configContent = "{\"express\": {\"main\": \"$$test_hot_reload.txt\"}}";
            Files.write(Paths.get(configPath), configContent.getBytes(StandardCharsets.UTF_8));

            // 第一次加载
            Map<String, Object> config1 = ConfigFileLoader.loadConfigFile("topezadmin/config/layui/dsl/list/test_hot_config.json");
            ExpressFileLoader.processExpressReferences(config1, "topezadmin/config/layui/dsl/list/test_hot_config.json");

            @SuppressWarnings("unchecked")
            Map<String, Object> express1 = (Map<String, Object>) config1.get("express");
            String content1 = (String) express1.get("main");
            System.out.println("第一次加载内容: " + content1);
            assertEquals("应该加载初始内容", initialContent, content1);

            // 修改表达式文件
            String modifiedContent = "SELECT * FROM test_table WHERE status = 1 AND active = true";
            Files.write(Paths.get(expressFilePath), modifiedContent.getBytes(StandardCharsets.UTF_8));
            System.out.println("修改表达式文件内容");

            // 第二次加载（应该读取到新内容）
            Map<String, Object> config2 = ConfigFileLoader.loadConfigFile("topezadmin/config/layui/dsl/list/test_hot_config.json");
            ExpressFileLoader.processExpressReferences(config2, "topezadmin/config/layui/dsl/list/test_hot_config.json");

            @SuppressWarnings("unchecked")
            Map<String, Object> express2 = (Map<String, Object>) config2.get("express");
            String content2 = (String) express2.get("main");
            System.out.println("第二次加载内容: " + content2);
            assertEquals("应该加载修改后的内容", modifiedContent, content2);

            System.out.println("✅ 表达式文件热加载测试通过");

        } finally {
            // 清理测试文件
            try {
                Files.deleteIfExists(Paths.get(expressFilePath));
                Files.deleteIfExists(Paths.get(currentDir + "/src/main/resources/topezadmin/config/layui/dsl/list/test_hot_config.json"));
                System.out.println("测试文件已清理");
            } catch (Exception e) {
                // 忽略清理错误
            }
        }
    }

    /**
     * 测试数组中的表达式文件引用
     */
    @Test
    public void testArrayExpressFileReference() throws Exception {
        System.out.println("===== 测试数组中的表达式文件引用 =====");

        // 设置为开发模式（启用热加载）
        EzBootstrap.config().setSqlCache(false);

        // 加载配置文件
        String configPath = "topezadmin/config/layui/dsl/list/test-array-express.json";
        Map<String, Object> config = ConfigFileLoader.loadConfigFile(configPath);

        assertNotNull("配置文件应该成功加载", config);
        System.out.println("配置文件加载成功");

        // 处理前 - 检查 appendFoot 数组
        @SuppressWarnings("unchecked")
        List<Object> appendFootBefore = (List<Object>) config.get("appendFoot");
        assertNotNull("appendFoot 应该存在", appendFootBefore);
        assertEquals("appendFoot 应该有3个元素", 3, appendFootBefore.size());

        String footItem2Before = (String) appendFootBefore.get(1);
        System.out.println("处理前 appendFoot[1]: " + footItem2Before);
        assertTrue("appendFoot[1] 应该以 $$ 开头", footItem2Before.startsWith("$$"));

        // 处理前 - 检查 customScripts 数组
        @SuppressWarnings("unchecked")
        List<Object> customScriptsBefore = (List<Object>) config.get("customScripts");
        assertNotNull("customScripts 应该存在", customScriptsBefore);

        String script1Before = (String) customScriptsBefore.get(0);
        String script3Before = (String) customScriptsBefore.get(2);
        System.out.println("处理前 customScripts[0]: " + script1Before);
        System.out.println("处理前 customScripts[2]: " + script3Before);
        assertTrue("customScripts[0] 应该以 $$ 开头", script1Before.startsWith("$$"));
        assertTrue("customScripts[2] 应该以 $$ 开头", script3Before.startsWith("$$"));

        // 处理表达式引用
        ExpressFileLoader.processExpressReferences(config, configPath);

        // 处理后 - 验证 appendFoot 数组
        @SuppressWarnings("unchecked")
        List<Object> appendFootAfter = (List<Object>) config.get("appendFoot");
        String footItem1After = (String) appendFootAfter.get(0);
        String footItem2After = (String) appendFootAfter.get(1);
        String footItem3After = (String) appendFootAfter.get(2);

        System.out.println("\n处理后 appendFoot:");
        System.out.println("  [0]: " + footItem1After);
        System.out.println("  [1]: " + (footItem2After.length() > 50 ? footItem2After.substring(0, 50) + "..." : footItem2After));
        System.out.println("  [2]: " + footItem3After);

        assertEquals("第1个元素应该保持不变", "<script>", footItem1After);
        assertFalse("第2个元素不应该再以 $$ 开头", footItem2After.startsWith("$$"));
        assertTrue("第2个元素应该包含加载的JS内容", footItem2After.contains("InfoList footer loaded"));
        assertEquals("第3个元素应该保持不变", "</script>", footItem3After);

        // 处理后 - 验证 customScripts 数组
        @SuppressWarnings("unchecked")
        List<Object> customScriptsAfter = (List<Object>) config.get("customScripts");
        String script1After = (String) customScriptsAfter.get(0);
        String script2After = (String) customScriptsAfter.get(1);
        String script3After = (String) customScriptsAfter.get(2);

        System.out.println("\n处理后 customScripts:");
        System.out.println("  [0]: " + script1After);
        System.out.println("  [1]: " + script2After);
        System.out.println("  [2]: " + script3After);

        assertFalse("第1个脚本不应该再以 $$ 开头", script1After.startsWith("$$"));
        assertTrue("第1个脚本应该包含加载的内容", script1After.contains("Script 1 loaded"));
        assertEquals("第2个脚本应该保持不变", "console.log('inline script');", script2After);
        assertFalse("第3个脚本不应该再以 $$ 开头", script3After.startsWith("$$"));
        assertTrue("第3个脚本应该包含加载的内容", script3After.contains("Script 2 loaded"));

        // 验证嵌套结构
        @SuppressWarnings("unchecked")
        Map<String, Object> nestedExample = (Map<String, Object>) config.get("nestedExample");
        @SuppressWarnings("unchecked")
        List<Object> items = (List<Object>) nestedExample.get("items");

        String item1 = (String) items.get(0);
        String item2 = (String) items.get(1);
        @SuppressWarnings("unchecked")
        Map<String, Object> item3 = (Map<String, Object>) items.get(2);

        assertEquals("普通字符串应该保持不变", "normal string", item1);
        assertFalse("引用文件应该被替换", item2.startsWith("$$"));
        assertTrue("应该包含文件内容", item2.contains("nested_item.txt"));

        String nestedContent = (String) item3.get("content");
        assertFalse("嵌套对象中的引用也应该被替换", nestedContent.startsWith("$$"));
        assertTrue("应该包含嵌套对象的文件内容", nestedContent.contains("nested object file"));

        System.out.println("\n✅ 数组中的表达式文件引用测试通过");
    }

    /**
     * 测试 appendHead 和 appendFoot 数组拼接功能
     */
    @Test
    public void testAppendFieldsArrayConcatenation() throws Exception {
        System.out.println("===== 测试 appendHead 和 appendFoot 数组拼接 =====");

        // 设置为开发模式（启用热加载）
        EzBootstrap.config().setSqlCache(false);

        // 加载配置文件
        String configPath = "topezadmin/config/layui/dsl/list/test-array-express.json";
        Map<String, Object> config = ConfigFileLoader.loadConfigFile(configPath);

        assertNotNull("配置文件应该成功加载", config);

        // 先处理表达式引用
        ExpressFileLoader.processExpressReferences(config, configPath);

        // 验证处理前的数据类型
        Object appendFootBeforeConcat = config.get("appendFoot");
        Object appendHeadBeforeConcat = config.get("appendHead");
        assertTrue("appendFoot 处理前应该是 List", appendFootBeforeConcat instanceof List);
        assertTrue("appendHead 处理前应该是 List", appendHeadBeforeConcat instanceof List);

        // 调用 processAppendFields 进行数组拼接
        ExpressFileLoader.processAppendFields(config, configPath);

        // 验证处理后的数据类型和内容
        Object appendFootAfterConcat = config.get("appendFoot");
        Object appendHeadAfterConcat = config.get("appendHead");

        // appendFoot 应该变成字符串
        assertTrue("appendFoot 处理后应该是 String", appendFootAfterConcat instanceof String);
        String footString = (String) appendFootAfterConcat;
        System.out.println("\n处理后的 appendFoot 字符串:");
        System.out.println("---开始---");
        System.out.println(footString);
        System.out.println("---结束---");

        // 验证内容包含所有元素，并且用换行符分隔
        assertTrue("应该包含 <script> 标签", footString.contains("<script>"));
        assertTrue("应该包含 </script> 标签", footString.contains("</script>"));
        assertTrue("应该包含加载的JS内容", footString.contains("InfoList footer loaded"));

        // 验证结构：第一行是 <script>，最后一行是 </script>
        String[] footLines = footString.split("\n");
        assertTrue("应该至少有3行（包括JS内容）", footLines.length >= 3);
        assertEquals("第一行应该是 <script>", "<script>", footLines[0]);
        assertEquals("最后一行应该是 </script>", "</script>", footLines[footLines.length - 1]);

        // appendHead 应该变成字符串
        assertTrue("appendHead 处理后应该是 String", appendHeadAfterConcat instanceof String);
        String headString = (String) appendHeadAfterConcat;
        System.out.println("\n处理后的 appendHead 字符串:");
        System.out.println("---开始---");
        System.out.println(headString);
        System.out.println("---结束---");

        String[] headLines = headString.split("\n");
        assertEquals("appendHead 应该有3行", 3, headLines.length);
        assertEquals("第一行应该是 <style>", "<style>", headLines[0]);
        assertEquals("第二行应该包含CSS", "  .custom-header { background: #f0f0f0; }", headLines[1]);
        assertEquals("第三行应该是 </style>", "</style>", headLines[2]);

        System.out.println("\n✅ appendHead 和 appendFoot 数组拼接测试通过");
    }
}