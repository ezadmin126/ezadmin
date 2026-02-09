package top.ezadmin;

import org.junit.Test;
import top.ezadmin.common.utils.Resources;
import java.io.File;

/**
 * 测试项目根路径检测
 */
public class TestProjectRootDetection {

    @Test
    public void testProjectRootDetection() {
        System.out.println("===== 测试项目根路径检测 =====");

        // 1. 显示当前工作目录
        String workingDir = System.getProperty("user.dir");
        System.out.println("当前工作目录: " + workingDir);

        // 2. 显示类路径根目录
        try {
            String classPath = Resources.getResourceURL("/").getPath();
            System.out.println("类路径根目录: " + classPath);

            // 处理 Windows 路径
            if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
                classPath = classPath.substring(1);
                System.out.println("Windows 处理后: " + classPath);
            }

            // 查找 target 目录
            int targetIndex = classPath.indexOf("target");
            if (targetIndex > 0) {
                String projectRoot = classPath.substring(0, targetIndex);
                if (projectRoot.endsWith(File.separator)) {
                    projectRoot = projectRoot.substring(0, projectRoot.length() - 1);
                }
                System.out.println("从 target 推算的项目根路径: " + projectRoot);

                // 验证路径
                File rootDir = new File(projectRoot);
                System.out.println("路径存在: " + rootDir.exists());
                System.out.println("是目录: " + rootDir.isDirectory());

                // 检查标志性文件
                File pomFile = new File(rootDir, "pom.xml");
                File srcDir = new File(rootDir, "src/main/resources");
                System.out.println("pom.xml 存在: " + pomFile.exists());
                System.out.println("src/main/resources 存在: " + srcDir.exists());
            } else {
                System.out.println("类路径中未找到 target 目录");
            }

        } catch (Exception e) {
            System.err.println("获取类路径失败: " + e.getMessage());
            e.printStackTrace();
        }

        // 3. 测试热加载配置
        System.out.println("\n===== 测试热加载配置 =====");
        EzBootstrapConfig config = new EzBootstrapConfig();

        // 设置为开发模式
        config.setSqlCache(false);
        System.out.println("sqlCache 设置为: false (热加载模式)");

        // 初始化
        EzBootstrap.getInstance().init(config);
        System.out.println("EzBootstrap 初始化完成");

        // 测试加载配置文件
        try {
            String testResource = "topezadmin/config/layui/dsl/list/test-list.json";
            File projectFile = new File(workingDir + "/src/main/resources/" + testResource);
            if (projectFile.exists()) {
                System.out.println("✅ 找到项目源文件: " + projectFile.getAbsolutePath());
            } else {
                System.out.println("❌ 项目源文件不存在: " + projectFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
        }
    }
}