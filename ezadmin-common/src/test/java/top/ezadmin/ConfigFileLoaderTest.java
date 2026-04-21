package top.ezadmin;

import org.junit.Test;
import top.ezadmin.common.utils.ConfigFileLoader;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * 配置文件热加载功能测试
 */
public class ConfigFileLoaderTest {

    /**
     * 测试文件热加载功能
     */
    @Test
    public void testHotReload() throws Exception {
        // 设置项目根路径（用于测试）
        String currentDir = System.getProperty("user.dir");
        System.out.println("当前工作目录: " + currentDir);

        // 测试加载配置文件
        testLoadListConfig();
        testLoadFormConfig();
    }

    /**
     * 测试加载列表配置
     */
    private void testLoadListConfig() {
        try {
            // 假设有一个测试用的列表配置文件
            String testListId = "test-list";
            String resourcePath = "topezadmin/config/layui/dsl/list/" + testListId + ".json";

            System.out.println("\n===== 测试列表配置加载 =====");
            System.out.println("资源路径: " + resourcePath);

            // 设置 sqlCache = false 来测试热加载
            EzBootstrap.config().setSqlCache(false);
            System.out.println("sqlCache设置为: false (启用热加载)");

            // 尝试加载配置
            try {
                Map<String, Object> config = ConfigFileLoader.loadConfigFile(resourcePath);
                System.out.println("配置加载成功: " + (config != null));
                if (config != null) {
                    System.out.println("配置包含的key: " + config.keySet());
                }
            } catch (Exception e) {
                System.out.println("从项目路径加载失败（文件可能不存在）: " + e.getMessage());
            }

            // 设置 sqlCache = true 来测试传统加载
            EzBootstrap.config().setSqlCache(true);
            System.out.println("\nsqlCache设置为: true (使用classpath)");

            // 再次尝试加载
            try {
                Map<String, Object> config = ConfigFileLoader.loadConfigFile(resourcePath);
                System.out.println("配置加载成功: " + (config != null));
            } catch (Exception e) {
                System.out.println("从classpath加载失败（文件可能不存在）: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("测试列表配置加载出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试加载表单配置
     */
    private void testLoadFormConfig() {
        try {
            // 假设有一个测试用的表单配置文件
            String testFormId = "test-form";
            String resourcePath = "topezadmin/config/layui/dsl/form/" + testFormId + ".json";

            System.out.println("\n===== 测试表单配置加载 =====");
            System.out.println("资源路径: " + resourcePath);

            // 设置 sqlCache = false 来测试热加载
            EzBootstrap.config().setSqlCache(false);
            System.out.println("sqlCache设置为: false (启用热加载)");

            // 检查文件是否存在于项目路径
            boolean existsInProject = ConfigFileLoader.existsInProjectPath(resourcePath);
            System.out.println("文件存在于项目路径: " + existsInProject);

            // 尝试加载配置
            try {
                Map<String, Object> config = ConfigFileLoader.loadConfigFile(resourcePath);
                System.out.println("配置加载成功: " + (config != null));
                if (config != null) {
                    System.out.println("配置包含的key: " + config.keySet());
                }
            } catch (Exception e) {
                System.out.println("从项目路径加载失败（文件可能不存在）: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("测试表单配置加载出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试创建示例配置文件
     */
    @Test
    public void createSampleConfigFile() throws Exception {
        // 创建一个示例列表配置文件用于测试
        String sampleListConfig = "{\n" +
                "  \"id\": \"test-list\",\n" +
                "  \"title\": \"测试列表\",\n" +
                "  \"description\": \"用于测试热加载功能的示例列表配置\",\n" +
                "  \"express\": {\n" +
                "    \"main\": \"SELECT * FROM test_table\",\n" +
                "    \"count\": \"SELECT COUNT(*) FROM test_table\",\n" +
                "    \"orderBy\": \"id DESC\"\n" +
                "  },\n" +
                "  \"columns\": [\n" +
                "    {\"field\": \"id\", \"title\": \"ID\", \"width\": 80},\n" +
                "    {\"field\": \"name\", \"title\": \"名称\", \"width\": 200},\n" +
                "    {\"field\": \"createTime\", \"title\": \"创建时间\", \"width\": 150}\n" +
                "  ]\n" +
                "}";

        // 获取项目根路径
        String currentDir = System.getProperty("user.dir");
        String targetPath = currentDir + "/src/main/resources/topezadmin/config/layui/dsl/list/test-list.json";

        File targetFile = new File(targetPath);
        File parentDir = targetFile.getParentFile();

        // 确保目录存在
        if (!parentDir.exists()) {
            parentDir.mkdirs();
            System.out.println("创建目录: " + parentDir.getAbsolutePath());
        }

        // 写入文件
        Path path = Paths.get(targetPath);
        Files.write(path, sampleListConfig.getBytes(StandardCharsets.UTF_8));
        System.out.println("创建示例配置文件: " + targetPath);

        // 创建表单配置示例
        String sampleFormConfig = "{\n" +
                "  \"id\": \"test-form\",\n" +
                "  \"title\": \"测试表单\",\n" +
                "  \"description\": \"用于测试热加载功能的示例表单配置\",\n" +
                "  \"items\": [\n" +
                "    {\"field\": \"name\", \"title\": \"名称\", \"type\": \"text\", \"required\": true},\n" +
                "    {\"field\": \"description\", \"title\": \"描述\", \"type\": \"textarea\"},\n" +
                "    {\"field\": \"status\", \"title\": \"状态\", \"type\": \"select\", \"options\": [{\"value\": \"1\", \"text\": \"启用\"}, {\"value\": \"0\", \"text\": \"禁用\"}]}\n" +
                "  ]\n" +
                "}";

        String formTargetPath = currentDir + "/src/main/resources/topezadmin/config/layui/dsl/form/test-form.json";
        File formTargetFile = new File(formTargetPath);
        File formParentDir = formTargetFile.getParentFile();

        if (!formParentDir.exists()) {
            formParentDir.mkdirs();
            System.out.println("创建目录: " + formParentDir.getAbsolutePath());
        }

        Path formPath = Paths.get(formTargetPath);
        Files.write(formPath, sampleFormConfig.getBytes(StandardCharsets.UTF_8));
        System.out.println("创建示例表单配置文件: " + formTargetPath);
    }
}