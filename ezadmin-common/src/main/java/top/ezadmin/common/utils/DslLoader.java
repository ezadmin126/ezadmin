package top.ezadmin.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.EzBootstrap;
import top.ezadmin.dao.dto.DslConfig;
import top.ezadmin.plugins.cache.Callback;
import top.ezadmin.service.DslDbService;

import java.util.Map;

/**
 * DSL 统一加载器
 * 实现"文件优先，数据库降级"的加载策略
 *
 * 加载顺序：
 * 1. 优先尝试从文件加载（支持热加载）
 * 2. 如果文件不存在，尝试从数据库加载
 * 3. 都不存在则返回null
 */
public class DslLoader {
    private static final Logger log = LoggerFactory.getLogger(DslLoader.class);
    private static final DslDbService dbService = DslDbService.getInstance();

    /**
     * 加载DSL配置（统一入口）
     * @param id DSL ID
     * @param type DSL类型：form 或 list
     * @return DSL配置对象，如果不存在则返回null
     */
    public static DslConfig loadDsl(String id, String type) {
        if (id == null || id.isEmpty()) {
            log.warn("DSL ID不能为空");
            return null;
        }
        if (!"form".equals(type) && !"list".equals(type)) {
            log.warn("不支持的DSL类型: {}", type);
            return null;
        }
        return (DslConfig)EzBootstrap.config().getEzCache().get("TEMPLATE_DSL", type + "-" + id,
                new Callback() {
                    @Override
                    public Object call(String key) {
                        return loadDslDirect(id, type);
                    }
                });
    }
    private static DslConfig loadDslDirect(String id, String type) {
        if (id == null || id.isEmpty()) {
            log.warn("DSL ID不能为空");
            return null;
        }
        if (!"form".equals(type) && !"list".equals(type)) {
            log.warn("不支持的DSL类型: {}", type);
            return null;
        }
        // 1. 优先尝试从文件加载
        DslConfig fileConfig = loadFromFile(id, type);
        if (fileConfig != null) {
            log.info("✓ 从文件加载DSL成功: id={}, type={}", id, type);
            return fileConfig;
        }

        // 2. 尝试从数据库加载
        DslConfig dbConfig = loadFromDatabase(id, type);
        if (dbConfig != null) {
            log.info("✓ 从数据库加载DSL成功: id={}, type={}", id, type);
            return dbConfig;
        }

        // 3. 都不存在
        log.warn("✗ DSL不存在: id={}, type={}", id, type);
        return null;
    }


    /**
     * 从文件加载DSL配置
     * @param id DSL ID（文件名）
     * @param type DSL类型
     * @return DSL配置对象，如果不存在则返回null
     */
    private static DslConfig loadFromFile(String id, String type) {
        try {
            String subPath = "form".equals(type) ? "form" : "list";
            String configPath = "topezadmin/config/layui/dsl/" + subPath + "/" + id + ".json";

            // 尝试加载文件
            Map<String, Object> dslConfig = ConfigFileLoader.loadConfigFile(configPath);
            if (dslConfig == null || dslConfig.isEmpty()) {
                log.debug("文件不存在或为空: {}", configPath);
                return null;
            }

            // 处理表达式文件引用（$xxx.txt）
            ExpressFileLoader.processExpressReferences(dslConfig, configPath);

            // 处理 appendHead 和 appendFoot 数组（支持表达式文件引用）
            ExpressFileLoader.processAppendFields(dslConfig, configPath);

            // 构建DslConfig对象
            DslConfig config = new DslConfig(id, type, dslConfig, "file");
            config.setDatasource(Utils.trimNull(dslConfig.get("dataSource")));

            log.debug("文件加载成功: {}", configPath);
            return config;

        } catch (Exception e) {
            log.debug("从文件加载DSL失败: id={}, type={}, error={}", id, type, e.getMessage());
            return null;
        }
    }

    /**
     * 从数据库加载DSL配置
     * @param id DSL ID
     * @param type DSL类型
     * @return DSL配置对象，如果不存在则返回null
     */
    private static DslConfig loadFromDatabase(String id, String type) {
        try {
            if ("form".equals(type)) {
                return dbService.loadFormFromDatabase(id);
            } else if ("list".equals(type)) {
                return dbService.loadListFromDatabase(id);
            }
            return null;
        } catch (Exception e) {
            log.warn("从数据库加载DSL失败: id={}, type={}", id, type, e);
            return null;
        }
    }

    /**
     * 保存DSL配置
     * @param id DSL ID
     * @param type DSL类型
     * @param dslConfig DSL配置内容
     * @param saveToFile 是否保存到文件
     * @param saveToDatabase 是否保存到数据库
     * @return 保存是否成功
     */
    public static boolean saveDsl(String id, String type, Map<String, Object> dslConfig,
                                  boolean saveToFile, boolean saveToDatabase) {
        boolean success = true;

        // 保存到文件
        if (saveToFile) {
            try {
                String subPath = "form".equals(type) ? "form" : "list";
                String projectRoot = ProjectPathUtils.getProjectRoot();
                if (projectRoot != null) {
                    String filePath = projectRoot + "/src/main/resources/topezadmin/config/layui/dsl/"
                                    + subPath + "/" + id + ".json";
                    java.io.File file = new java.io.File(filePath);

                    // 确保父目录存在
                    if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                    }

                    // 格式化 JSON 并写入文件
                    String formattedJson = JSONUtils.toPrettyJSONString(dslConfig);
                    java.nio.file.Files.write(java.nio.file.Paths.get(filePath),
                                            formattedJson.getBytes(java.nio.charset.StandardCharsets.UTF_8));

                    log.info("✓ DSL保存到文件成功: {}", filePath);
                } else {
                    log.warn("无法获取项目根路径，跳过文件保存");
                    success = false;
                }
            } catch (Exception e) {
                log.error("保存DSL到文件失败: id={}, type={}", id, type, e);
                success = false;
            }
        }

        // 保存到数据库
        if (saveToDatabase) {
            try {
                boolean dbSuccess = false;
                if ("form".equals(type)) {
                    dbSuccess = dbService.saveFormToDatabase(id, dslConfig);
                } else if ("list".equals(type)) {
                    dbSuccess = dbService.saveListToDatabase(id, dslConfig);
                }

                if (dbSuccess) {
                    log.info("✓ DSL保存到数据库成功: id={}, type={}", id, type);
                } else {
                    log.error("✗ DSL保存到数据库失败: id={}, type={}", id, type);
                    success = false;
                }
            } catch (Exception e) {
                log.error("保存DSL到数据库失败: id={}, type={}", id, type, e);
                success = false;
            }
        }

        return success;
    }
}
