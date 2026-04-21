package top.ezadmin.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * 项目路径工具类
 * 提供获取项目根路径的统一方法
 */
public class ProjectPathUtils {

    private static final Logger logger = LoggerFactory.getLogger(ProjectPathUtils.class);

    // 缓存项目根路径，避免重复计算
    private static volatile String cachedProjectRoot = null;

    /**
     * 获取项目根路径
     *
     * @return 项目根路径，如果无法确定则返回 null
     */
    public static String getProjectRoot() {
        // 使用双重检查锁定模式实现缓存
        if (cachedProjectRoot != null) {
            return cachedProjectRoot;
        }

        synchronized (ProjectPathUtils.class) {
            if (cachedProjectRoot != null) {
                return cachedProjectRoot;
            }

            String projectRoot = doGetProjectRoot();
            cachedProjectRoot = projectRoot;
            return projectRoot;
        }
    }

    /**
     * 清除缓存的项目根路径
     * 用于测试或需要重新检测项目路径的场景
     */
    public static void clearCache() {
        cachedProjectRoot = null;
    }

    /**
     * 实际获取项目根路径的逻辑
     */
    private static String doGetProjectRoot() {
        // 方法1：通过系统属性获取（如果在开发时设置了）
        String projectRoot = System.getProperty("ezadmin.project.root");
        if (projectRoot != null && new File(projectRoot).exists()) {
            logger.debug("Project root found from system property: {}", projectRoot);
            return projectRoot;
        }

        // 方法2：基于类路径反推项目根路径（参考 JsoupUtil.editPath 实现）
        try {
            String classPath = Resources.getResourceURL("/").getPath();

            // 处理 Windows 路径
            if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
                classPath = classPath.substring(1);
            }

            // 查找 target 目录位置（Maven 构建输出目录）
            int targetIndex = classPath.indexOf("target");
            if (targetIndex > 0) {
                // 获取项目根路径（target 的上级目录）
                String rootPath = classPath.substring(0, targetIndex);

                // 移除末尾的文件分隔符
                if (rootPath.endsWith(File.separator)) {
                    rootPath = rootPath.substring(0, rootPath.length() - 1);
                }

                // 验证项目根路径
                File rootDir = new File(rootPath);
                if (isProjectRoot(rootDir)) {
                    logger.debug("Project root found from target path: {}", rootPath);
                    return rootPath;
                }
            }

            // 查找 build 目录位置（Gradle 构建输出目录）
            int buildIndex = classPath.indexOf("build");
            if (buildIndex > 0) {
                String rootPath = classPath.substring(0, buildIndex);
                if (rootPath.endsWith(File.separator)) {
                    rootPath = rootPath.substring(0, rootPath.length() - 1);
                }

                File rootDir = new File(rootPath);
                if (isProjectRoot(rootDir)) {
                    logger.debug("Project root found from build path: {}", rootPath);
                    return rootPath;
                }
            }

        } catch (Exception e) {
            logger.debug("Failed to get project root from class path: {}", e.getMessage());
        }

        // 方法3：通过当前工作目录推断
        String workingDir = System.getProperty("user.dir");
        if (workingDir != null) {
            File workingDirFile = new File(workingDir);

            // 检查当前目录是否是项目根目录
            if (isProjectRoot(workingDirFile)) {
                logger.debug("Project root found from working directory: {}", workingDir);
                return workingDir;
            }

            // 向上查找项目根目录（最多向上5层）
            File currentDir = workingDirFile;
            for (int i = 0; i < 5; i++) {
                currentDir = currentDir.getParentFile();
                if (currentDir == null) {
                    break;
                }
                if (isProjectRoot(currentDir)) {
                    String absolutePath = currentDir.getAbsolutePath();
                    logger.debug("Project root found by traversing up: {}", absolutePath);
                    return absolutePath;
                }
            }

            // 检查是否在子模块中（如 ezadmin-common）
            if (workingDir.contains("ezadmin-common")) {
                String parentPath = workingDir.substring(0, workingDir.indexOf("ezadmin-common") + "ezadmin-common".length());
                File parentDir = new File(parentPath);
                if (parentDir.exists() && isProjectRoot(parentDir)) {
                    logger.debug("Project root found from submodule path: {}", parentPath);
                    return parentPath;
                }
            }
        }

        logger.debug("Unable to determine project root path");
        return null;
    }

    /**
     * 判断目录是否为项目根目录
     *
     * @param dir 要检查的目录
     * @return 是否为项目根目录
     */
    public static boolean isProjectRoot(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return false;
        }

        // 检查是否存在标志性文件或目录
        File pomFile = new File(dir, "pom.xml");
        File gradleFile = new File(dir, "build.gradle");
        File srcDir = new File(dir, "src/main/resources");

        return (pomFile.exists() || gradleFile.exists()) && srcDir.exists();
    }

    /**
     * 获取项目文件的完整路径
     *
     * @param relativePath 相对于项目根目录的路径
     * @return 完整的文件系统路径，如果无法确定则返回 null
     */
    public static String getProjectFilePath(String relativePath) {
        String projectRoot = getProjectRoot();
        if (projectRoot == null) {
            return null;
        }
        return projectRoot + File.separator + relativePath;
    }

    /**
     * 获取资源文件的完整路径
     *
     * @param resourcePath classpath 资源路径，例如 "topezadmin/config/layui/dsl/form/test.json"
     * @return 完整的文件系统路径，如果无法确定则返回 null
     */
    public static String getResourceFilePath(String resourcePath) {
        return getProjectFilePath("src/main/resources/" + resourcePath);
    }

    /**
     * 检查资源文件是否存在于项目路径中
     *
     * @param resourcePath classpath 资源路径
     * @return 文件是否存在
     */
    public static boolean resourceExistsInProject(String resourcePath) {
        String filePath = getResourceFilePath(resourcePath);
        if (filePath == null) {
            return false;
        }
        return new File(filePath).exists();
    }
}
