package top.ezadmin;

import org.junit.BeforeClass;
import org.junit.Test;
import top.ezadmin.common.utils.ConfigFileLoader;
import top.ezadmin.common.utils.ExpressFileLoader;
import top.ezadmin.dao.dto.BaseDsl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * appendHead 和 appendFoot 功能单元测试
 * 测试列表与表单中的 appendHead 和 appendFoot 字段的处理逻辑
 * <p>
 * 功能说明：
 * 1. appendHead/appendFoot 可以是字符串或数组
 * 2. 如果是数组，会被拼接成换行分隔的字符串
 * 3. 数组元素支持 $$ 表达式引用外部文件
 */
public class AppendFieldsTest {

    @BeforeClass
    public static void setup() {
        // 设置为开发模式，启用热加载
        EzBootstrapConfig config = new EzBootstrapConfig();
        config.setSqlCache(false);
        EzBootstrap.getInstance().init(config);
    }

    /**
     * 测试 appendHead 字符串形式
     */
    @Test
    public void testAppendHeadAsString() {
        System.out.println("=== testAppendHeadAsString ===");

        Map<String, Object> config = new HashMap<>();
        config.put("appendHead", "<style>.header { color: red; }</style>");

        // appendHead 本身就是字符串，不需要处理
        ExpressFileLoader.processAppendFields(config, "topezadmin/config/layui/dsl/list/test.json");

        Object result = config.get("appendHead");
        assertTrue("appendHead should be a String", result instanceof String);
        assertEquals("appendHead should match", "<style>.header { color: red; }</style>", result);

        System.out.println("appendHead (String): " + result);
    }

    /**
     * 测试 appendFoot 字符串形式
     */
    @Test
    public void testAppendFootAsString() {
        System.out.println("=== testAppendFootAsString ===");

        Map<String, Object> config = new HashMap<>();
        config.put("appendFoot", "<script>console.log('footer');</script>");

        ExpressFileLoader.processAppendFields(config, "topezadmin/config/layui/dsl/list/test.json");

        Object result = config.get("appendFoot");
        assertTrue("appendFoot should be a String", result instanceof String);
        assertEquals("appendFoot should match", "<script>console.log('footer');</script>", result);

        System.out.println("appendFoot (String): " + result);
    }

    /**
     * 测试 appendHead 数组形式（不含表达式引用）
     */
    @Test
    public void testAppendHeadAsArray() {
        System.out.println("=== testAppendHeadAsArray ===");

        Map<String, Object> config = new HashMap<>();
        List<Object> appendHead = new ArrayList<>();
        appendHead.add("<style>");
        appendHead.add("  .custom-header { background: #f0f0f0; }");
        appendHead.add("  .custom-title { font-size: 18px; }");
        appendHead.add("</style>");
        config.put("appendHead", appendHead);

        // 处理前是数组
        assertTrue("appendHead should be a List before processing", config.get("appendHead") instanceof List);

        ExpressFileLoader.processAppendFields(config, "topezadmin/config/layui/dsl/list/test.json");

        // 处理后是字符串
        Object result = config.get("appendHead");
        assertTrue("appendHead should be a String after processing", result instanceof String);

        String expected = "<style>\n" +
                "  .custom-header { background: #f0f0f0; }\n" +
                "  .custom-title { font-size: 18px; }\n" +
                "</style>";
        assertEquals("appendHead should be joined with newlines", expected, result);

        System.out.println("appendHead (Array -> String):");
        System.out.println(result);
    }

    /**
     * 测试 appendFoot 数组形式（不含表达式引用）
     */
    @Test
    public void testAppendFootAsArray() {
        System.out.println("=== testAppendFootAsArray ===");

        Map<String, Object> config = new HashMap<>();
        List<Object> appendFoot = new ArrayList<>();
        appendFoot.add("<script>");
        appendFoot.add("  console.log('page loaded');");
        appendFoot.add("  console.log('init complete');");
        appendFoot.add("</script>");
        config.put("appendFoot", appendFoot);

        ExpressFileLoader.processAppendFields(config, "topezadmin/config/layui/dsl/list/test.json");

        Object result = config.get("appendFoot");
        assertTrue("appendFoot should be a String after processing", result instanceof String);

        String expected = "<script>\n" +
                "  console.log('page loaded');\n" +
                "  console.log('init complete');\n" +
                "</script>";
        assertEquals("appendFoot should be joined with newlines", expected, result);

        System.out.println("appendFoot (Array -> String):");
        System.out.println(result);
    }

    /**
     * 测试空数组
     */
    @Test
    public void testEmptyAppendArray() {
        System.out.println("=== testEmptyAppendArray ===");

        Map<String, Object> config = new HashMap<>();
        config.put("appendHead", new ArrayList<>());
        config.put("appendFoot", new ArrayList<>());

        ExpressFileLoader.processAppendFields(config, "topezadmin/config/layui/dsl/list/test.json");

        assertEquals("Empty appendHead should become empty string", "", config.get("appendHead"));
        assertEquals("Empty appendFoot should become empty string", "", config.get("appendFoot"));

        System.out.println("Empty arrays converted to empty strings");
    }

    /**
     * 测试从配置文件加载包含数组表达式的 appendHead 和 appendFoot
     */
    @Test
    public void testLoadArrayExpressFromConfigFile() throws Exception {
        System.out.println("=== testLoadArrayExpressFromConfigFile ===");

        String configPath = "topezadmin/config/layui/dsl/list/test-array-express.json";
        Map<String, Object> config = ConfigFileLoader.loadConfigFile(configPath);

        assertNotNull("Config should not be null", config);

        // 验证加载后的原始数据
        Object appendHeadObj = config.get("appendHead");
        Object appendFootObj = config.get("appendFoot");

        assertTrue("appendHead should be a List", appendHeadObj instanceof List);
        assertTrue("appendFoot should be a List", appendFootObj instanceof List);

        @SuppressWarnings("unchecked")
        List<Object> appendHead = (List<Object>) appendHeadObj;
        @SuppressWarnings("unchecked")
        List<Object> appendFoot = (List<Object>) appendFootObj;

        System.out.println("Original appendHead array:");
        for (int i = 0; i < appendHead.size(); i++) {
            System.out.println("  [" + i + "]: " + appendHead.get(i));
        }

        System.out.println("\nOriginal appendFoot array:");
        for (int i = 0; i < appendFoot.size(); i++) {
            System.out.println("  [" + i + "]: " + appendFoot.get(i));
        }

        // 处理表达式引用（将 $$filename 替换为文件内容）
        ExpressFileLoader.processExpressReferences(config, configPath);

        System.out.println("\nAfter processing express references:");
        System.out.println("appendFoot[1] (after $$ replacement): " +
                ((List<?>) config.get("appendFoot")).get(1));

        // 处理数组拼接（将数组转为换行分隔的字符串）
        ExpressFileLoader.processAppendFields(config, configPath);

        // 验证处理后的结果
        Object processedHead = config.get("appendHead");
        Object processedFoot = config.get("appendFoot");

        assertTrue("Processed appendHead should be a String", processedHead instanceof String);
        assertTrue("Processed appendFoot should be a String", processedFoot instanceof String);

        String headStr = (String) processedHead;
        String footStr = (String) processedFoot;

        // 验证 appendHead 包含所有行
        assertTrue("appendHead should contain <style>", headStr.contains("<style>"));
        assertTrue("appendHead should contain CSS class", headStr.contains(".custom-header"));

        // 验证 appendFoot 包含所有行
        assertTrue("appendFoot should contain <script>", footStr.contains("<script>"));
        assertTrue("appendFoot should contain </script>", footStr.contains("</script>"));

        System.out.println("\nFinal appendHead string:");
        System.out.println(headStr);

        System.out.println("\nFinal appendFoot string (first 300 chars):");
        if (footStr.length() > 300) {
            System.out.println(footStr.substring(0, 300) + "...");
        } else {
            System.out.println(footStr);
        }
    }

    /**
     * 测试 BaseDsl 对象的 getAppendHead 和 getAppendFoot 方法
     */
    @Test
    public void testBaseDslAppendMethods() {
        System.out.println("=== testBaseDslAppendMethods ===");

        // 测试 appendHead 作为字符串
        BaseDsl dsl1 = new BaseDsl();
        dsl1.setAppendHead("<style>.test { color: blue; }</style>");
        assertEquals("getAppendHead should return string",
                "<style>.test { color: blue; }</style>", dsl1.getAppendHead());

        // 测试 appendFoot 作为字符串
        BaseDsl dsl2 = new BaseDsl();
        dsl2.setAppendFoot("<script>console.log('test');</script>");
        assertEquals("getAppendFoot should return string",
                "<script>console.log('test');</script>", dsl2.getAppendFoot());

        System.out.println("String appendHead: " + dsl1.getAppendHead());
        System.out.println("String appendFoot: " + dsl2.getAppendFoot());
    }

    /**
     * 测试包含 null 元素的数组
     */
    @Test
    public void testAppendArrayWithNullElements() {
        System.out.println("=== testAppendArrayWithNullElements ===");

        Map<String, Object> config = new HashMap<>();
        List<Object> appendHead = new ArrayList<>();
        appendHead.add("<style>");
        appendHead.add(null);  // null 元素
        appendHead.add("  .test { color: red; }");
        appendHead.add("</style>");
        config.put("appendHead", appendHead);

        ExpressFileLoader.processAppendFields(config, "topezadmin/config/layui/dsl/list/test.json");

        String result = (String) config.get("appendHead");
        assertTrue("Result should be a String", result instanceof String);

        // null 元素会被跳过或处理为空字符串
        System.out.println("appendHead with null elements:");
        System.out.println(result);

        // 验证结果包含非 null 元素
        assertTrue("Should contain <style>", result.contains("<style>"));
        assertTrue("Should contain CSS", result.contains(".test"));
    }

    /**
     * 测试混合内容（数组 + 表达式引用）
     */
    @Test
    public void testMixedContentArray() {
        System.out.println("=== testMixedContentArray ===");

        Map<String, Object> config = new HashMap<>();
        List<Object> appendFoot = new ArrayList<>();
        appendFoot.add("<script>");
        appendFoot.add("  var config = { debug: true };");
        // 如果有表达式文件，它会被处理；这里用普通字符串模拟
        appendFoot.add("  console.log('Mixed content test');");
        appendFoot.add("</script>");
        config.put("appendFoot", appendFoot);

        ExpressFileLoader.processAppendFields(config, "topezadmin/config/layui/dsl/list/test.json");

        String result = (String) config.get("appendFoot");

        System.out.println("Mixed content appendFoot:");
        System.out.println(result);

        assertTrue("Should contain script tag", result.contains("<script>"));
        assertTrue("Should contain config", result.contains("var config"));
        assertTrue("Should contain console.log", result.contains("console.log"));
    }

    /**
     * 测试不存在 appendHead 和 appendFoot 字段的情况
     */
    @Test
    public void testMissingAppendFields() {
        System.out.println("=== testMissingAppendFields ===");

        Map<String, Object> config = new HashMap<>();
        config.put("id", "test");
        config.put("name", "Test Config");
        // 没有 appendHead 和 appendFoot

        ExpressFileLoader.processAppendFields(config, "topezadmin/config/layui/dsl/list/test.json");

        assertFalse("appendHead should not be added", config.containsKey("appendHead"));
        assertFalse("appendFoot should not be added", config.containsKey("appendFoot"));

        System.out.println("Config without append fields processed successfully");
    }

    /**
     * 测试 appendHead 和 appendFoot 同时存在的完整场景
     */
    @Test
    public void testBothAppendFieldsTogether() {
        System.out.println("=== testBothAppendFieldsTogether ===");

        Map<String, Object> config = new HashMap<>();

        // appendHead 数组
        List<Object> appendHead = new ArrayList<>();
        appendHead.add("<style>");
        appendHead.add("  body { margin: 0; padding: 0; }");
        appendHead.add("  .container { max-width: 1200px; }");
        appendHead.add("</style>");
        config.put("appendHead", appendHead);

        // appendFoot 数组
        List<Object> appendFoot = new ArrayList<>();
        appendFoot.add("<script>");
        appendFoot.add("  $(document).ready(function() {");
        appendFoot.add("    console.log('Document ready');");
        appendFoot.add("  });");
        appendFoot.add("</script>");
        config.put("appendFoot", appendFoot);

        ExpressFileLoader.processAppendFields(config, "topezadmin/config/layui/dsl/list/test.json");

        String headResult = (String) config.get("appendHead");
        String footResult = (String) config.get("appendFoot");

        assertNotNull("appendHead should not be null", headResult);
        assertNotNull("appendFoot should not be null", footResult);

        System.out.println("appendHead:");
        System.out.println(headResult);
        System.out.println("\nappendFoot:");
        System.out.println(footResult);

        // 验证内容
        assertTrue("appendHead should have 4 lines", headResult.split("\n").length == 4);
        assertTrue("appendFoot should have 5 lines", footResult.split("\n").length == 5);
    }
}
