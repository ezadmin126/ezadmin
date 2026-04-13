package top.ezadmin;

import org.junit.Test;
import top.ezadmin.plugins.templates.ThymeleafEzTemplate;
import top.ezadmin.plugins.templates.TemplateUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 模板热加载功能测试
 */
public class TemplateHotReloadTest {

    /**
     * 测试模板热加载功能
     */
    @Test
    public void testTemplateHotReload() throws Exception {
        System.out.println("===== 测试模板热加载功能 =====");

        // 获取当前工作目录
        String currentDir = System.getProperty("user.dir");
        System.out.println("当前工作目录: " + currentDir);

        // 测试不同的 sqlCache 设置
        testWithSqlCache(true);
        testWithSqlCache(false);
    }

    /**
     * 根据 sqlCache 设置测试模板加载
     */
    private void testWithSqlCache(boolean sqlCache) {
        System.out.println("\n===== 测试 sqlCache = " + sqlCache + " =====");

        // 设置配置
        EzBootstrapConfig config = new EzBootstrapConfig();
        config.setSqlCache(sqlCache);

        // 初始化模板引擎
        ThymeleafEzTemplate template = new ThymeleafEzTemplate();
        template.init(config);

        // 测试渲染模板
        Map<String, Object> data = new HashMap<>();
        data.put("title", "测试标题");
        data.put("content", "这是测试内容");

        try {
            // 尝试渲染一个简单的模板
            String result = template.renderFile("layui/dsl/component/input", data);
            System.out.println("模板渲染结果长度: " + (result != null ? result.length() : 0));

            if (sqlCache) {
                System.out.println("使用 classpath 加载模板（缓存模式）");
            } else {
                System.out.println("使用文件系统加载模板（热加载模式）");
            }

            // 清理缓存
            template.clearCache();
            System.out.println("缓存已清理");

        } catch (Exception e) {
            System.err.println("模板渲染失败: " + e.getMessage());
        }
    }

    /**
     * 创建测试用的组件模板
     */
    @Test
    public void createTestComponentTemplate() throws Exception {
        System.out.println("===== 创建测试组件模板 =====");

        // 创建一个测试用的组件模板
        String testTemplate = "<!DOCTYPE html>\n" +
                "<html xmlns:th=\"http://www.thymeleaf.org\">\n" +
                "<div class=\"test-component\">\n" +
                "    <label th:text=\"${title}\">标题</label>\n" +
                "    <input type=\"text\" th:name=\"${field}\" th:value=\"${value}\" />\n" +
                "    <span class=\"help-text\" th:text=\"${help}\">帮助文本</span>\n" +
                "    <!-- 这是一个测试组件模板，用于验证热加载功能 -->\n" +
                "</div>\n" +
                "</html>";

        // 获取项目根路径
        String currentDir = System.getProperty("user.dir");
        String targetPath = currentDir + "/src/main/resources/topezadmin/config/layui/dsl/component/test-component.html";

        File targetFile = new File(targetPath);
        File parentDir = targetFile.getParentFile();

        // 确保目录存在
        if (!parentDir.exists()) {
            parentDir.mkdirs();
            System.out.println("创建目录: " + parentDir.getAbsolutePath());
        }

        // 写入文件
        Path path = Paths.get(targetPath);
        Files.write(path, testTemplate.getBytes(StandardCharsets.UTF_8));
        System.out.println("创建测试组件模板: " + targetPath);
        System.out.println("模板内容:\n" + testTemplate);
    }

    /**
     * 测试 TemplateUtils 的组件渲染
     */
    @Test
    public void testTemplateUtilsRender() {
        System.out.println("===== 测试 TemplateUtils 组件渲染 =====");

        // 设置为热加载模式
        EzBootstrap.config().setSqlCache(false);
        System.out.println("设置 sqlCache = false (热加载模式)");

        // 初始化模板引擎
        ThymeleafEzTemplate.getIntance().init(EzBootstrap.config());

        // 创建 TemplateUtils 实例
        TemplateUtils utils = new TemplateUtils();

        // 准备组件参数
        Map<String, Object> param = new HashMap<>();
        param.put("component", "input"); // 使用 input 组件
        param.put("field", "username");
        param.put("title", "用户名");
        param.put("props", new HashMap<String, Object>() {{
            put("placeholder", "请输入用户名");
            put("required", true);
        }});

        try {
            // 渲染组件
            String result = utils.render(param);
            System.out.println("组件渲染成功");
            System.out.println("渲染结果长度: " + result.length());

            // 显示部分结果（前200个字符）
            if (result.length() > 200) {
                System.out.println("渲染结果预览: " + result.substring(0, 200) + "...");
            } else {
                System.out.println("渲染结果: " + result);
            }

        } catch (Exception e) {
            System.err.println("组件渲染失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试模板文件修改后的热加载
     */
    @Test
    public void testHotReloadAfterModification() throws Exception {
        System.out.println("===== 测试模板修改后的热加载 =====");

        // 设置为热加载模式
        EzBootstrap.config().setSqlCache(false);
        ThymeleafEzTemplate template = new ThymeleafEzTemplate();
        template.init(EzBootstrap.config());

        String currentDir = System.getProperty("user.dir");
        String testTemplatePath = currentDir + "/src/main/resources/topezadmin/config/layui/dsl/component/test-hot-reload.html";

        // 创建初始模板
        String initialTemplate = "<div>初始版本: [[${message}]]</div>";
        File templateFile = new File(testTemplatePath);
        templateFile.getParentFile().mkdirs();
        Files.write(Paths.get(testTemplatePath), initialTemplate.getBytes(StandardCharsets.UTF_8));
        System.out.println("创建初始模板: " + testTemplatePath);

        // 渲染初始版本
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Hello World");

        try {
            String result1 = template.renderFile("layui/dsl/component/test-hot-reload", data);
            System.out.println("初始渲染结果: " + result1);

            // 修改模板
            String modifiedTemplate = "<div class=\"modified\">修改后版本: <strong>[[${message}]]</strong></div>";
            Files.write(Paths.get(testTemplatePath), modifiedTemplate.getBytes(StandardCharsets.UTF_8));
            System.out.println("模板已修改");

            // 清理缓存（如果需要）
            template.clearCache();

            // 再次渲染
            String result2 = template.renderFile("layui/dsl/component/test-hot-reload", data);
            System.out.println("修改后渲染结果: " + result2);

            // 验证是否加载了新模板
            if (result2.contains("修改后版本")) {
                System.out.println("✅ 热加载成功！模板已更新");
            } else {
                System.out.println("❌ 热加载失败！模板未更新");
            }

            // 清理测试文件
            templateFile.delete();
            System.out.println("测试文件已清理");

        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}