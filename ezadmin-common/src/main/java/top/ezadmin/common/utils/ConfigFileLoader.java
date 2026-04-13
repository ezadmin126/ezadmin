package top.ezadmin.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.EzBootstrap;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * 配置文件加载工具类
 * 当 sqlCache 为 false 时，直接从项目路径读取文件，实现热加载
 * 当 sqlCache 为 true 时，从 classpath 读取文件
 */
public class ConfigFileLoader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigFileLoader.class);

    /**
     * 加载配置文件内容并解析为 Map
     * @param resourcePath classpath 资源路径，例如 "topezadmin/config/layui/dsl/list/xxx.json"
     * @return 解析后的 Map 对象
     * @throws Exception 读取或解析失败时抛出异常
     */
    public static Map<String, Object> loadConfigFile(String resourcePath) throws Exception {
        String jsonContent = loadConfigFileContent(resourcePath);
        return JSONUtils.deepParseObjectMap(jsonContent);
    }

    /**
     * 加载配置文件内容
     * @param resourcePath classpath 资源路径
     * @return 文件内容字符串
     * @throws Exception 读取失败时抛出异常
     */
    public static String loadConfigFileContent(String resourcePath) throws Exception {
        // 判断是否启用缓存
        if (!EzBootstrap.config().isSqlCache()) {
            // 开发模式：直接从项目路径读取文件
            String fileContent = loadFromProjectPath(resourcePath);
            if (fileContent != null) {
                logger.debug("Loaded config from project path (hot reload enabled): {}", resourcePath);
                return fileContent;
            }
        }

        // 生产模式或文件不存在：从 classpath 读取
        logger.debug("Loaded config from classpath: {}", resourcePath);
        return Resources.getResourceAsString(resourcePath);
    }

    /**
     * 获取项目源文件的完整路径（用于模板文件）
     * @param resourcePath classpath 资源路径
     * @return 完整的文件系统路径，如果无法确定则返回 null
     */
    public static String getProjectFilePath(String resourcePath) {
        return ProjectPathUtils.getResourceFilePath(resourcePath);
    }

    /**
     * 从项目路径直接读取文件
     * @param resourcePath classpath 资源路径
     * @return 文件内容，如果文件不存在则返回 null
     */
    private static String loadFromProjectPath(String resourcePath) {
        try {
            // 获取项目根路径
            String projectRoot = ProjectPathUtils.getProjectRoot();
            if (projectRoot == null) {
                return null;
            }

            // 构建文件路径
            String filePath = projectRoot + "/src/main/resources/" + resourcePath;
            Path path = Paths.get(filePath);

            // 检查文件是否存在
            if (!Files.exists(path)) {
                logger.debug("File not found in project path: {}", filePath);
                return null;
            }

            // 读取文件内容
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes, StandardCharsets.UTF_8);

        } catch (IOException e) {
            logger.warn("Failed to load file from project path: {}", resourcePath, e);
            return null;
        }
    }

    /**
     * 检查配置文件是否存在于项目路径中
     * @param resourcePath classpath 资源路径
     * @return 文件是否存在
     */
    public static boolean existsInProjectPath(String resourcePath) {
        return ProjectPathUtils.resourceExistsInProject(resourcePath);
    }
}