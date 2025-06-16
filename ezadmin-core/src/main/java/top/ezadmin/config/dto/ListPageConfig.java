package top.ezadmin.config.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListPageConfig {
    private String id;
    /**
     * 页面属性
     */
    private Map<String,String> attr=new HashMap<>();
    private String header;
    private List<TabConfig> tabs;
    private List<FieldConfig> searchForm;
    private List<ButtonConfig> tableButtons;
    private TableConfig table;
    private String selectSql;
    private String groupBySql;
    private String orderBySql;
    private String countSql;
    private String footer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public List<TabConfig> getTabs() {
        return tabs;
    }

    public void setTabs(List<TabConfig> tabs) {
        this.tabs = tabs;
    }

    public List<FieldConfig> getSearchForm() {
        return searchForm;
    }

    public void setSearchForm(List<FieldConfig> searchForm) {
        this.searchForm = searchForm;
    }

    public List<ButtonConfig> getTableButtons() {
        return tableButtons;
    }

    public void setTableButtons(List<ButtonConfig> tableButtons) {
        this.tableButtons = tableButtons;
    }

    public TableConfig getTable() {
        return table;
    }

    public void setTable(TableConfig table) {
        this.table = table;
    }

    public String getSelectSql() {
        return selectSql;
    }

    public void setSelectSql(String selectSql) {
        this.selectSql = selectSql;
    }

    public String getCountSql() {
        return countSql;
    }

    public void setCountSql(String countSql) {
        this.countSql = countSql;
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public Map<String, String> getAttr() {
        return attr;
    }

    public void setAttr(Map<String, String> attr) {
        this.attr = attr;
    }

    public String getGroupBySql() {
        return groupBySql;
    }

    public void setGroupBySql(String groupBySql) {
        this.groupBySql = groupBySql;
    }

    public String getOrderBySql() {
        return orderBySql;
    }

    public void setOrderBySql(String orderBySql) {
        this.orderBySql = orderBySql;
    }
}