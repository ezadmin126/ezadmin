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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表达式文件加载工具类
 * 支持在JSON配置中通过 $filename 引用外部文件作为表达式内容
 */
public class ExpressFileLoader {

    private static final Logger logger = LoggerFactory.getLogger(ExpressFileLoader.class);

    /**
     * 处理配置中的所有表达式引用
     * 将以 $ 开头的值替换为对应文件的内容
     *
     * @param config 配置对象
     * @param configPath 配置文件的路径，用于确定相对路径
     */
    public static void processExpressReferences(Map<String, Object> config, String configPath) {
        if (config == null || configPath == null) {
            return;
        }

        // 处理列表的 express 节点
        if (config.containsKey("express")) {
            Object expressObj = config.get("express");
            if (expressObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> expressMap = (Map<String, Object>) expressObj;
                processExpressMap(expressMap, configPath);
            }
        }

        // 处理表单的各种表达式字段（兼容老的配置方式）
        processExpressField(config, "select_express", configPath);
        processExpressField(config, "count_express", configPath);
        processExpressField(config, "orderby_express", configPath);
        processExpressField(config, "groupby_express", configPath);
        processExpressField(config, "init_express", configPath);
        processExpressField(config, "submit_express", configPath);
        processExpressField(config, "delete_express", configPath);
        processExpressField(config, "status_express", configPath);
        processExpressField(config, "displayorder_express", configPath);

        // 处理所有字段（包括普通字段和嵌套结构）
        for (Map.Entry<String, Object> entry : config.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            // 处理字符串值
            if (value instanceof String) {
                String strValue = (String) value;
                if (strValue.startsWith("$")) {
                    String fileName = strValue.substring(1);
                    String content = loadExpressFile(fileName, configPath);
                    if (content != null) {
                        config.put(key, content);
                        logger.debug("Loaded express from file: {} for field: {}", fileName, key);
                    }
                }
            }
            // 处理嵌套的对象
            else if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) value;
                processExpressReferences(nestedMap, configPath);
            }
            // 处理数组
            else if (value instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) value;
                processExpressListReferences(list, configPath);
            }
        }
    }

    /**
     * 处理 express 映射中的所有字段
     */
    private static void processExpressMap(Map<String, Object> expressMap, String configPath) {
        // 处理列表相关的 express 字段
        processExpressField(expressMap, "main", configPath);
        processExpressField(expressMap, "count", configPath);
        processExpressField(expressMap, "orderBy", configPath);
        processExpressField(expressMap, "groupBy", configPath);
        processExpressField(expressMap, "select", configPath);
        processExpressField(expressMap, "where", configPath);
        processExpressField(expressMap, "having", configPath);

        // 处理表单相关的 express 字段
        processExpressField(expressMap, "init", configPath);
        processExpressField(expressMap, "submit", configPath);
        processExpressField(expressMap, "delete", configPath);
        processExpressField(expressMap, "status", configPath);
    }

    /**
     * 处理单个表达式字段
     */
    private static void processExpressField(Map<String, Object> map, String fieldName, String configPath) {
        if (!map.containsKey(fieldName)) {
            return;
        }

        Object value = map.get(fieldName);
        if (value instanceof String) {
            String strValue = (String) value;
            if (strValue.startsWith("$")) {
                // 去掉 $ 前缀，获取文件名
                String fileName = strValue.substring(1);
                String content = loadExpressFile(fileName, configPath);
                if (content != null) {
                    map.put(fieldName, content);
                    logger.debug("Loaded express from file: {} for field: {}", fileName, fieldName);
                }
            }
        }
    }

    /**
     * 处理数组中的表达式引用
     * 遍历数组，将以 $ 开头的字符串元素替换为对应文件的内容
     *
     * @param list 要处理的数组
     * @param configPath 配置文件路径，用于确定相对路径
     */
    private static void processExpressListReferences(List<Object> list, String configPath) {
        if (list == null || list.isEmpty()) {
            return;
        }

        for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);
            if (item instanceof String) {
                String strValue = (String) item;
                if (strValue.startsWith("$")) {
                    // 去掉 $ 前缀，获取文件名
                    String fileName = strValue.substring(1);
                    String content = loadExpressFile(fileName, configPath);
                    if (content != null) {
                        list.set(i, content);
                        logger.debug("Loaded express from file: {} for list item at index: {}", fileName, i);
                    }
                }
            } else if (item instanceof Map) {
                // 递归处理嵌套的 Map
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) item;
                processExpressReferences(nestedMap, configPath);
            } else if (item instanceof List) {
                // 递归处理嵌套的 List
                @SuppressWarnings("unchecked")
                List<Object> nestedList = (List<Object>) item;
                processExpressListReferences(nestedList, configPath);
            }
        }
    }

    /**
     * 加载表达式文件内容
     *
     * @param fileName 文件名
     * @param configPath 配置文件路径，用于确定相对路径
     * @return 文件内容，如果读取失败返回 null
     */
    private static String loadExpressFile(String fileName, String configPath) {
        try {
            // 获取配置文件所在目录
            String configDir = getConfigDirectory(configPath);

            // 构建表达式文件的完整路径
            String expressFilePath = configDir + "/" + fileName;

            // 如果启用了热加载，尝试从项目路径加载
            if (!EzBootstrap.config().isSqlCache()) {
                String content = loadFromProjectPath(expressFilePath);
                if (content != null) {
                    logger.debug("Loaded express file from project path (hot reload): {}", expressFilePath);
                    return content;
                }
            }

            // 从 classpath 加载
            try {
                String content = Resources.getResourceAsString(expressFilePath);
                logger.debug("Loaded express file from classpath: {}", expressFilePath);
                return content;
            } catch (Exception e) {
                logger.warn("Failed to load express file from classpath: {}", expressFilePath);
            }

        } catch (Exception e) {
            logger.error("Failed to load express file: {}", fileName, e);
        }

        return null;
    }

    /**
     * 从项目路径直接加载文件
     */
    private static String loadFromProjectPath(String resourcePath) {
        try {
            String projectRoot = getProjectRoot();
            if (projectRoot == null) {
                return null;
            }

            String filePath = projectRoot + "/src/main/resources/" + resourcePath;
            Path path = Paths.get(filePath);

            if (!Files.exists(path)) {
                return null;
            }

            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes, StandardCharsets.UTF_8);

        } catch (IOException e) {
            logger.debug("Failed to load express file from project path: {}", resourcePath, e.getMessage());
            return null;
        }
    }

    /**
     * 获取项目根路径（复用 ConfigFileLoader 的逻辑）
     */
    private static String getProjectRoot() {
        // 优先使用系统属性
        String projectRoot = System.getProperty("ezadmin.project.root");
        if (projectRoot != null && new File(projectRoot).exists()) {
            return projectRoot;
        }

        // 基于类路径反推
        try {
            String classPath = Resources.getResourceURL("/").getPath();

            if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
                classPath = classPath.substring(1);
            }

            int targetIndex = classPath.indexOf("target");
            if (targetIndex > 0) {
                String rootPath = classPath.substring(0, targetIndex);
                if (rootPath.endsWith(File.separator)) {
                    rootPath = rootPath.substring(0, rootPath.length() - 1);
                }

                File rootDir = new File(rootPath);
                if (isProjectRoot(rootDir)) {
                    return rootPath;
                }
            }

        } catch (Exception e) {
            logger.debug("Failed to get project root from class path: {}", e.getMessage());
        }

        // 使用当前工作目录
        String workingDir = System.getProperty("user.dir");
        if (workingDir != null) {
            File workingDirFile = new File(workingDir);
            if (isProjectRoot(workingDirFile)) {
                return workingDir;
            }
        }

        return null;
    }

    /**
     * 判断目录是否为项目根目录
     */
    private static boolean isProjectRoot(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return false;
        }

        File pomFile = new File(dir, "pom.xml");
        File gradleFile = new File(dir, "build.gradle");
        File srcDir = new File(dir, "src/main/resources");

        return (pomFile.exists() || gradleFile.exists()) && srcDir.exists();
    }

    /**
     * 获取配置文件所在目录
     * 例如：topezadmin/config/layui/dsl/list/test.json -> topezadmin/config/layui/dsl/list
     */
    private static String getConfigDirectory(String configPath) {
        int lastSlash = configPath.lastIndexOf('/');
        if (lastSlash > 0) {
            return configPath.substring(0, lastSlash);
        }
        return "";
    }

    /**
     * 批量处理多个配置对象的表达式引用
     */
    public static void processMultipleConfigs(Map<String, Map<String, Object>> configs, String baseConfigPath) {
        if (configs == null) {
            return;
        }

        for (Map.Entry<String, Map<String, Object>> entry : configs.entrySet()) {
            String configName = entry.getKey();
            Map<String, Object> config = entry.getValue();

            // 为每个配置构建完整路径
            String fullConfigPath = baseConfigPath + "/" + configName + ".json";
            processExpressReferences(config, fullConfigPath);
        }
    }

    /**
     * 处理 appendHead 和 appendFoot 字段
     * 如果这些字段是数组，则将数组元素拼接成换行的字符串
     *
     * @param config 配置对象
     */
    public static void processAppendFields(Map<String, Object> config) {
        if (config == null) {
            return;
        }

        // 处理 appendHead
        processAppendField(config, "appendHead");

        // 处理 appendFoot
        processAppendField(config, "appendFoot");
    }

    /**
     * 处理单个 append 字段
     * 如果字段是数组，则将数组元素用换行符拼接成字符串
     *
     * @param config 配置对象
     * @param fieldName 字段名（appendHead 或 appendFoot）
     */
    private static void processAppendField(Map<String, Object> config, String fieldName) {
        Object value = config.get(fieldName);
        if (value instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) value;

            if (!list.isEmpty()) {
                // 将数组元素拼接成字符串，每个元素一行
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) {
                        sb.append("\n");
                    }
                    Object item = list.get(i);
                    if (item != null) {
                        sb.append(item.toString());
                    }
                }

                // 替换原来的数组为拼接后的字符串
                config.put(fieldName, sb.toString());
                logger.debug("Converted {} array to string with {} lines", fieldName, list.size());
            }
        }
    }
}