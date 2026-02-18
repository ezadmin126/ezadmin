package top.ezadmin.dao.dto;


/**
 * DSL 修改请求
 */

public class DslModificationRequest {

    /**
     * DSL 文件名（不含路径和扩展名），例如：student-form, student-list
     */
    private String dslFileName;

    /**
     * DSL 类型：form（表单）或 list（列表）
     */
    private String dslType;

    /**
     * 用户的自然语言需求描述
     */
    private String userRequirement;

    /**
     * 可选：选中的元素信息（来自 EZ Inspector）
     */
    private String item_name;

    private String componentCode;

    private String selectedComponent;

    private String dataSource;

    public String getDslFileName() {
        return dslFileName;
    }

    public void setDslFileName(String dslFileName) {
        this.dslFileName = dslFileName;
    }

    public String getDslType() {
        return dslType;
    }

    public void setDslType(String dslType) {
        this.dslType = dslType;
    }

    public String getUserRequirement() {
        return userRequirement;
    }

    public void setUserRequirement(String userRequirement) {
        this.userRequirement = userRequirement;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getComponentCode() {
        return componentCode;
    }

    public void setComponentCode(String componentCode) {
        this.componentCode = componentCode;
    }

    public String getSelectedComponent() {
        return selectedComponent;
    }

    public void setSelectedComponent(String selectedComponent) {
        this.selectedComponent = selectedComponent;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}
