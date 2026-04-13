package top.ezadmin.dao.dto;

import java.util.Map;

/**
 * DSL 配置数据传输对象
 * 用于统一文件和数据库两种数据源的DSL配置
 */
public class DslConfig {

    /**
     * DSL ID（文件名或数据库主键）
     */
    private String id;

    /**
     * DSL 类型：form（表单）或 list（列表）
     */
    private String type;

    /**
     * DSL 配置内容（Map格式）
     */
    private Map<String, Object> config;

    /**
     * 数据来源：file（文件）或 database（数据库）
     */
    private String source;

    /**
     * 数据源名称（用于数据库连接）
     */
    private String datasource;

    public DslConfig() {
    }

    public DslConfig(String id, String type, Map<String, Object> config, String source) {
        this.id = id;
        this.type = type;
        this.config = config;
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    /**
     * 判断是否来自文件
     */
    public boolean isFromFile() {
        return "file".equals(source);
    }

    /**
     * 判断是否来自数据库
     */
    public boolean isFromDatabase() {
        return "database".equals(source);
    }

    @Override
    public String toString() {
        return "DslConfig{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", source='" + source + '\'' +
                ", datasource='" + datasource + '\'' +
                ", config=" + (config != null ? config.size() + " fields" : "null") +
                '}';
    }
}
