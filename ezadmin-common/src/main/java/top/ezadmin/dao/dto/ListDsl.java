package top.ezadmin.dao.dto;

import top.ezadmin.common.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 列表 DSL 配置类
 * 对应 layui dsl/list 配置文件
 */
public class ListDsl extends BaseDsl {


    /**
     * 是否隐藏搜索栏
     */
    private Boolean hideSearch = false;
    /**
     * 初始化数据接口
     */
    private String initApi;
    private String countApi;
    /**
     * 表格主体配置
     */
    private Map<String, Object> body;

    /**
     * 搜索配置列表
     */
    private List<SearchRow> search;

    /**
     * 列配置列表
     */
    private List<ColumnItem> column;

    /**
     * 表格工具栏按钮列表
     */
    private List<ButtonItem> tableButton;

    /**
     * 行操作按钮列表
     */
    private List<ButtonItem> rowButton;

    /**
     * SQL表达式配置
     */
    private Express express;


    public List<SearchRow> getSearch() {
        return search;
    }

    public void setSearch(List<SearchRow> search) {
        this.search = search;
    }

    public List<ColumnItem> getColumn() {
        return column;
    }

    public void setColumn(List<ColumnItem> column) {
        this.column = column;
    }

    public List<ButtonItem> getTableButton() {
        return tableButton;
    }

    public void setTableButton(List<ButtonItem> tableButton) {
        this.tableButton = tableButton;
    }

    public List<ButtonItem> getRowButton() {
        return rowButton;
    }

    public void setRowButton(List<ButtonItem> rowButton) {
        this.rowButton = rowButton;
    }

    public Express getExpress() {
        return express;
    }

    public void setExpress(Express express) {
        this.express = express;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }

    public Boolean getHideSearch() {
        return hideSearch;
    }

    public void setHideSearch(Boolean hideSearch) {
        this.hideSearch = hideSearch;
    }

    public String getInitApi() {
        return initApi;
    }

    public void setInitApi(String initApi) {
        this.initApi = initApi;
    }

    public String getCountApi() {
        return countApi;
    }

    public void setCountApi(String countApi) {
        this.countApi = countApi;
    }

    /**
     * 搜索行配置
     */
    public static class SearchRow {
        /**
         * 搜索项列表
         */
        private List<SearchItem> row;

        public List<SearchItem> getRow() {
            return row;
        }

        public void setRow(List<SearchItem> row) {
            this.row = row;
        }
    }

    /**
     * 搜索项配置
     */
    public static class SearchItem {
        /**
         * 字段名称
         */
        private String item_name;

        /**
         * 显示标签
         */
        private String label;

        /**
         * 组件类型（input, select, cascader, date等）
         */
        private String component;

        /**
         * 操作符（like, EQ, in, BETWEEN等）
         */
        private String operator;
        /**
         * JDBC类型
         */
        private String jdbcType;

        /**
         * 初始化数据配置
         */
        private InitData initData;

        /**
         * 组件属性配置（使用HashMap确保Fastjson反序列化为HashMap而非JSONObject）
         */
        private HashMap<String, Object> props;

        public String getItem_name() {
            return item_name;
        }

        public void setItem_name(String item_name) {
            this.item_name = item_name;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getComponent() {
            return component;
        }

        public void setComponent(String component) {
            this.component = component;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public InitData getInitData() {
            return initData;
        }

        public void setInitData(InitData initData) {
            this.initData = initData;
        }

        public HashMap<String, Object> getProps() {
            return props;
        }

        @SuppressWarnings("unchecked")
        public void setProps(HashMap<String, Object> props) {
            // 递归转换所有嵌套的 JSONObject 为 HashMap
            this.props = (HashMap<String, Object>) convertToHashMap(props);
        }

        public String getJdbcType() {
            return jdbcType;
        }

        public void setJdbcType(String jdbcType) {
            this.jdbcType = jdbcType;
        }
    }

    /**
     * 列配置项
     */
    public static class ColumnItem {
        /**
         * 字段名称
         */
        private String item_name;

        /**
         * 显示标签
         */
        private String label;

        /**
         * 组件类型（tdSelect, tdCascader, tdPic, tdText等）
         */
        private String component;

        /**
         * 初始化数据配置
         */
        private InitData initData;

        /**
         * 组件属性配置（使用HashMap确保Fastjson反序列化为HashMap而非JSONObject）
         */
        private HashMap<String, Object> props;

        public String getItem_name() {
            return item_name;
        }

        public void setItem_name(String item_name) {
            this.item_name = item_name;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getComponent() {
            return component;
        }

        public void setComponent(String component) {
            this.component = component;
        }

        public InitData getInitData() {
            return initData;
        }

        public void setInitData(InitData initData) {
            this.initData = initData;
        }

        public HashMap<String, Object> getProps() {
            return props;
        }

        @SuppressWarnings("unchecked")
        public void setProps(HashMap<String, Object> props) {
            // 递归转换所有嵌套的 JSONObject 为 HashMap
            this.props = (HashMap<String, Object>) convertToHashMap(props);
        }
    }

    /**
     * 按钮配置项
     */
    public static class ButtonItem {
        /**
         * 按钮唯一标识
         */
        private String item_name;

        /**
         * 按钮显示文本
         */
        private String label;

        /**
         * 组件类型（button-toolbar, button-normal等）
         */
        private String component;

        /**
         * 按钮属性配置（使用HashMap确保Fastjson反序列化为HashMap而非JSONObject）
         */
        private HashMap<String, Object> props;

        public String getItem_name() {
            return item_name;
        }

        public void setItem_name(String item_name) {
            this.item_name = item_name;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getComponent() {
            return component;
        }

        public void setComponent(String component) {
            this.component = component;
        }

        public HashMap<String, Object> getProps() {
            return props;
        }

        @SuppressWarnings("unchecked")
        public void setProps(HashMap<String, Object> props) {
            // 递归转换所有嵌套的 JSONObject 为 HashMap
            this.props = (HashMap<String, Object>) convertToHashMap(props);
        }
    }

    /**
     * 初始化数据配置
     */
    public static class InitData {
        /**
         * 数据源名称
         */
        private String dataSource;

        /**
         * 数据查询SQL
         */
        private String dataSql;

        /**
         * JSON格式的静态数据
         */
        private List<DataJsonItem> dataJson;

        public String getDataSource() {
            return dataSource;
        }

        public void setDataSource(String dataSource) {
            this.dataSource = dataSource;
        }

        public String getDataSql() {
            return dataSql;
        }

        public void setDataSql(String dataSql) {
            this.dataSql = dataSql;
        }

        public List<DataJsonItem> getDataJson() {
            return dataJson;
        }

        public void setDataJson(List<DataJsonItem> dataJson) {
            this.dataJson = dataJson;
        }
    }

    /**
     * JSON数据项
     */
    public static class DataJsonItem {
        /**
         * 显示标签
         */
        private String label;

        /**
         * 数据值
         */
        private String value;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * SQL表达式配置
     */
    public static class Express {
        /**
         * 主SQL语句（Java代码）
         */
        private Object main;
        /**
         * 计数SQL语句
         */
        private Object count;
        /**
         * 排序子句
         */
        private Object orderBy;

        /**
         * 分组子句
         */
        private Object groupBy;

        public Object getMain() {
            return main;
        }

        public void setMain(Object main) {
            this.main = main;
        }

        public Object getCount() {
            return count;
        }

        public void setCount(Object count) {
            this.count = count;
        }

        public String getOrderBy() {
            //如果是数组，则空格合并
            if (orderBy instanceof List) {
                return StringUtils.join((List<String>) orderBy, " ");
            }
            return (String) orderBy;
        }

        public void setOrderBy(Object orderBy) {
            this.orderBy = orderBy;
        }

        public String getGroupBy() {
            //如果是数组，则空格合并
            if (groupBy instanceof List) {
                return StringUtils.join((List<String>) groupBy, " ");
            }
            return (String) groupBy;
        }

        public void setGroupBy(Object groupBy) {
            this.groupBy = groupBy;
        }
    }
}
