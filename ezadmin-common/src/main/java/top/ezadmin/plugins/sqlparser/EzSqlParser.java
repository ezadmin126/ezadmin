package top.ezadmin.plugins.sqlparser;


public interface EzSqlParser {
    /**
     * sql转为ezadmin 列表配置
     *
     * @param sql
     * @param listId
     * @return String
     */
    String sqlToList(String sql, String listId, String datasource) throws Exception;

    /**
     * sql转为ezadmin 表单配置
     *
     * @param sql
     * @param formId
     * @return String
     */
    String sqlToForm(String sql, String formId, String datasource) throws Exception;
}
