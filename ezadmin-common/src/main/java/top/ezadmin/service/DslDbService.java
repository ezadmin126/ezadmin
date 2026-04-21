package top.ezadmin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.EzBootstrap;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.dao.Dao;
import top.ezadmin.dao.dto.DslConfig;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * DSL 数据库服务
 * 负责从数据库加载和保存DSL配置
 */
public class DslDbService {
    private static final Logger log = LoggerFactory.getLogger(DslDbService.class);
    private static final DslDbService instance = new DslDbService();

    private DslDbService() {
    }

    public static DslDbService getInstance() {
        return instance;
    }

    /**
     * 从数据库加载表单DSL配置
     *
     * @param formId 表单ID
     * @return DSL配置对象，如果不存在则返回null
     */
    public DslConfig loadFormFromDatabase(String formId) {
        try {
            String sql = "SELECT FORM_ID, FORM_NAME, FORM_DSL, INIT_EXPRESS, SUBMIT_EXPRESS, " +
                    "DELETE_EXPRESS, DATASOURCE FROM T_EZADMIN_FORM " +
                    "WHERE FORM_ID = ? AND DELETE_FLAG = 0";

            DataSource ds = EzBootstrap.getInstance().getEzDataSource();
            Map<String, Object> row = Dao.getInstance().executeQueryOne(ds, sql, new Object[]{formId});

            if (row == null || row.isEmpty()) {
                log.info("表单 {} 在数据库中不存在", formId);
                return null;
            }

            // 解析DSL配置
            String dslJson = Utils.trimNull(row.get("FORM_DSL"));
            Map<String, Object> dslConfig = JSONUtils.deepParseObjectMap(dslJson);

            // 组装表达式到DSL中
            dslConfig.put("id", Utils.trimNull(row.get("FORM_ID")));
            dslConfig.put("name", Utils.trimNull(row.get("FORM_NAME")));
            dslConfig.put("dataSource", Utils.trimNull(row.get("DATASOURCE")));
            dslConfig.put("initExpress", Utils.trimNull(row.get("INIT_EXPRESS")));
            dslConfig.put("submitExpress", Utils.trimNull(row.get("SUBMIT_EXPRESS")));
            dslConfig.put("deleteExpress", Utils.trimNull(row.get("DELETE_EXPRESS")));

            // 构建DslConfig对象
            DslConfig config = new DslConfig(formId, "form", dslConfig, "database");
            config.setDatasource(Utils.trimNull(row.get("DATASOURCE")));

            log.info("成功从数据库加载表单DSL: {}", formId);
            return config;

        } catch (Exception e) {
            //   log.error("从数据库加载表单DSL失败: {}", formId, e);
            return null;
        }
    }

    /**
     * 从数据库加载列表DSL配置
     *
     * @param listId 列表ID
     * @return DSL配置对象，如果不存在则返回null
     */
    public DslConfig loadListFromDatabase(String listId) {
        try {
            String sql = "SELECT LIST_ID, LIST_NAME, LIST_DSL, SELECT_EXPRESS, COUNT_EXPRESS, DATASOURCE " +
                    "FROM T_EZADMIN_LIST " +
                    "WHERE LIST_ID = ? AND DELETE_FLAG = 0";

            DataSource ds = EzBootstrap.getInstance().getEzDataSource();
            Map<String, Object> row = Dao.getInstance().executeQueryOne(ds, sql, new Object[]{listId});

            if (row == null || row.isEmpty()) {
                log.info("列表 {} 在数据库中不存在", listId);
                return null;
            }

            // 解析DSL配置
            String dslJson = Utils.trimNull(row.get("LIST_DSL"));
            Map<String, Object> dslConfig = JSONUtils.deepParseObjectMap(dslJson);

            // 组装表达式到DSL中
            dslConfig.put("id", Utils.trimNull(row.get("LIST_ID")));
            dslConfig.put("name", Utils.trimNull(row.get("LIST_NAME")));
            dslConfig.put("dataSource", Utils.trimNull(row.get("DATASOURCE")));

            // 构建 express 对象
            Map<String, Object> express = new HashMap<>();
            express.put("main", Utils.trimNull(row.get("SELECT_EXPRESS")));
            express.put("count", Utils.trimNull(row.get("COUNT_EXPRESS")));
            dslConfig.put("express", express);

            // 构建DslConfig对象
            DslConfig config = new DslConfig(listId, "list", dslConfig, "database");
            config.setDatasource(Utils.trimNull(row.get("DATASOURCE")));

            log.info("成功从数据库加载列表DSL: {}", listId);
            return config;

        } catch (Exception e) {
            //  log.error("从数据库加载列表DSL失败: {}", listId, e);
            return null;
        }
    }

    /**
     * 保存表单DSL到数据库
     *
     * @param formId    表单ID
     * @param dslConfig DSL配置Map
     * @return 保存是否成功
     */
    public boolean saveFormToDatabase(String formId, Map<String, Object> dslConfig) {
        try {
            DataSource ds = EzBootstrap.getInstance().getEzDataSource();

            // 提取表达式
            String initExpress = Utils.trimNull(dslConfig.get("initExpress"));
            String submitExpress = Utils.trimNull(dslConfig.get("submitExpress"));
            String deleteExpress = Utils.trimNull(dslConfig.get("deleteExpress"));
            String datasource = Utils.trimNull(dslConfig.get("dataSource"));
            String formName = Utils.trimNull(dslConfig.get("name"));

            // 移除表达式字段，只保存纯DSL配置
            Map<String, Object> pureDsl = new HashMap<>(dslConfig);
            pureDsl.remove("initExpress");
            pureDsl.remove("submitExpress");
            pureDsl.remove("deleteExpress");
            pureDsl.remove("dataSource");

            String dslJson = JSONUtils.toJSONString(pureDsl);

            // 检查是否已存在
            String checkSql = "SELECT ID FROM T_EZADMIN_FORM WHERE FORM_ID = ? AND DELETE_FLAG = 0";
            Map<String, Object> existing = Dao.getInstance().executeQueryOne(ds, checkSql, new Object[]{formId});

            if (existing != null && !existing.isEmpty()) {
                // 更新
                String updateSql = "UPDATE T_EZADMIN_FORM SET FORM_NAME = ?, FORM_DSL = ?, " +
                        "INIT_EXPRESS = ?, SUBMIT_EXPRESS = ?, DELETE_EXPRESS = ?, " +
                        "DATASOURCE = ?, UPDATE_TIME = NOW() " +
                        "WHERE FORM_ID = ? AND DELETE_FLAG = 0";
                Dao.getInstance().executeUpdate(ds, updateSql,
                        new Object[]{formName, dslJson, initExpress, submitExpress, deleteExpress, datasource, formId});
                log.info("成功更新表单DSL到数据库: {}", formId);
            } else {
                // 插入
                String insertSql = "INSERT INTO T_EZADMIN_FORM (FORM_ID, FORM_NAME, FORM_DSL, " +
                        "INIT_EXPRESS, SUBMIT_EXPRESS, DELETE_EXPRESS, DATASOURCE, " +
                        "ADD_TIME, DELETE_FLAG) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), 0)";
                Dao.getInstance().executeUpdate(ds, insertSql,
                        new Object[]{formId, formName, dslJson, initExpress, submitExpress, deleteExpress, datasource});
                log.info("成功插入表单DSL到数据库: {}", formId);
            }

            return true;

        } catch (Exception e) {
            log.error("保存表单DSL到数据库失败: {}", formId, e);
            return false;
        }
    }

    /**
     * 保存列表DSL到数据库
     *
     * @param listId    列表ID
     * @param dslConfig DSL配置Map
     * @return 保存是否成功
     */
    public boolean saveListToDatabase(String listId, Map<String, Object> dslConfig) {
        try {
            DataSource ds = EzBootstrap.getInstance().getEzDataSource();

            // 提取表达式
            Map<String, Object> express = (Map<String, Object>) dslConfig.get("express");
            String selectExpress = "";
            String countExpress = "";
            if (express != null) {
                selectExpress = Utils.trimNull(express.get("main"));
                countExpress = Utils.trimNull(express.get("count"));
            }

            String datasource = Utils.trimNull(dslConfig.get("dataSource"));
            String listName = Utils.trimNull(dslConfig.get("name"));

            // 移除表达式字段，只保存纯DSL配置
            Map<String, Object> pureDsl = new HashMap<>(dslConfig);
            pureDsl.remove("express");
            pureDsl.remove("dataSource");

            String dslJson = JSONUtils.toJSONString(pureDsl);

            // 检查是否已存在
            String checkSql = "SELECT ID FROM T_EZADMIN_LIST WHERE LIST_ID = ? AND DELETE_FLAG = 0";
            Map<String, Object> existing = Dao.getInstance().executeQueryOne(ds, checkSql, new Object[]{listId});

            if (existing != null && !existing.isEmpty()) {
                // 更新
                String updateSql = "UPDATE T_EZADMIN_LIST SET LIST_NAME = ?, LIST_DSL = ?, " +
                        "SELECT_EXPRESS = ?, COUNT_EXPRESS = ?, DATASOURCE = ?, " +
                        "UPDATE_TIME = NOW() " +
                        "WHERE LIST_ID = ? AND DELETE_FLAG = 0";
                Dao.getInstance().executeUpdate(ds, updateSql,
                        new Object[]{listName, dslJson, selectExpress, countExpress, datasource, listId});
                log.info("成功更新列表DSL到数据库: {}", listId);
            } else {
                // 插入
                String insertSql = "INSERT INTO T_EZADMIN_LIST (LIST_ID, LIST_NAME, LIST_DSL, " +
                        "SELECT_EXPRESS, COUNT_EXPRESS, DATASOURCE, " +
                        "ADD_TIME, DELETE_FLAG) " +
                        "VALUES (?, ?, ?, ?, ?, ?, NOW(), 0)";
                Dao.getInstance().executeUpdate(ds, insertSql,
                        new Object[]{listId, listName, dslJson, selectExpress, countExpress, datasource});
                log.info("成功插入列表DSL到数据库: {}", listId);
            }

            return true;

        } catch (Exception e) {
            log.error("保存列表DSL到数据库失败: {}", listId, e);
            return false;
        }
    }
}
