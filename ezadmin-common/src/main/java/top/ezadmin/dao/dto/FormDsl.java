package top.ezadmin.dao.dto;

import top.ezadmin.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 表单 DSL 配置类
 * 对应 layui dsl/form 配置文件
 */
public class FormDsl extends BaseDsl{

    /**
     * 表单提交成功后的跳转URL
     */
    private String successUrl;

    /**
     * 卡片列表
     */
    private List<CardItem> cardList;

    /**
     * 初始化表达式（用于加载表单数据）
     */
    private Object initExpress;

    /**
     * 提交表达式（用于保存表单数据）
     */
    private Object submitExpress;

    /**
     * 删除表达式（用于删除数据）
     */
    private Object deleteExpress;

    public String getSuccessUrl() {
        return successUrl;
    }

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    public List<CardItem> getCardList() {
        return cardList;
    }

    public void setCardList(List<CardItem> cardList) {
        this.cardList = cardList;
    }

    public String getInitExpress() {
        // 如果是数组，则换行合并
        if (initExpress instanceof List) {
            return StringUtils.join((List<String>) initExpress, "\n");
        }
        return (String) initExpress;
    }

    public void setInitExpress(Object initExpress) {
        this.initExpress = initExpress;
    }

    public String getSubmitExpress() {
        // 如果是数组，则换行合并
        if (submitExpress instanceof List) {
            return StringUtils.join((List<String>) submitExpress, "\n");
        }
        return (String) submitExpress;
    }

    public void setSubmitExpress(Object submitExpress) {
        this.submitExpress = submitExpress;
    }

    public String getDeleteExpress() {
        // 如果是数组，则换行合并
        if (deleteExpress instanceof List) {
            return StringUtils.join((List<String>) deleteExpress, "\n");
        }
        return (String) deleteExpress;
    }

    public void setDeleteExpress(Object deleteExpress) {
        this.deleteExpress = deleteExpress;
    }

    /**
     * 卡片配置项
     */
    public static class CardItem {
        /**
         * 卡片类型
         */
        private String type;

        /**
         * 卡片标签
         */
        private String label;

        /**
         * 卡片唯一标识
         */
        private String item_name;

        /**
         * 显示顺序
         */
        private Integer order;

        /**
         * 字段列表
         */
        private List<FieldRow> fieldList;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getItem_name() {
            return item_name;
        }

        public void setItem_name(String item_name) {
            this.item_name = item_name;
        }

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public List<FieldRow> getFieldList() {
            return fieldList;
        }

        public void setFieldList(List<FieldRow> fieldList) {
            this.fieldList = fieldList;
        }
    }

    /**
     * 字段行配置
     */
    public static class FieldRow {
        /**
         * 字段项列表（一行中可包含多个字段）
         */
        private List<FieldItem> row;

        public List<FieldItem> getRow() {
            return row;
        }

        public void setRow(List<FieldItem> row) {
            this.row = row;
        }
    }

    /**
     * 字段配置项
     */
    public static class FieldItem {
        /**
         * 字段名称
         */
        private String item_name;

        /**
         * 显示标签
         */
        private String label;

        /**
         * 组件类型（input, select, select-multiple, date, hidden等）
         */
        private String component;

        /**
         * CSS类追加
         */
        private String classAppend;

        /**
         * 组件属性配置（使用HashMap确保Fastjson反序列化为HashMap而非JSONObject）
         */
        private HashMap<String, Object> props;

        /**
         * 初始化数据配置
         */
        private InitData initData;

        /**
         * 数据列表
         */
        private List<DataJsonItem> data;

        /**
         * JSON格式的数据字符串
         */
        private String dataJson;

        /**
         * JSON格式的属性字符串
         */
        private String propsJson;

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

        public String getClassAppend() {
            return classAppend;
        }

        public void setClassAppend(String classAppend) {
            this.classAppend = classAppend;
        }

        public HashMap<String, Object> getProps() {
            return props;
        }

        @SuppressWarnings("unchecked")
        public void setProps(HashMap<String, Object> props) {
            // 递归转换所有嵌套的 JSONObject 为 HashMap
            this.props = (HashMap<String, Object>) convertToHashMap(props);
        }

        public InitData getInitData() {
            return initData;
        }

        public void setInitData(InitData initData) {
            this.initData = initData;
        }

        public List<DataJsonItem> getData() {
            return data;
        }

        public void setData(List<DataJsonItem> data) {
            this.data = data;
        }

        public String getDataJson() {
            return dataJson;
        }

        public void setDataJson(String dataJson) {
            this.dataJson = dataJson;
        }

        public String getPropsJson() {
            return propsJson;
        }

        public void setPropsJson(String propsJson) {
            this.propsJson = propsJson;
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
        private Object value;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}
