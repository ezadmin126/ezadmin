package com.ezadmin.biz.emmber.form;

/**
 *
 **/
public class EzFormVO {

    private String formId;
    private String encodeFormId;
    private String formName;
    private String dataSource;

    private String initExpress;
    private String submitExpress;

    private String appendHead;
    private String appendFoot;

    private String templateBodyForm;

    private String groupData;


    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getEncodeFormId() {
        return encodeFormId;
    }

    public void setEncodeFormId(String encodeFormId) {
        this.encodeFormId = encodeFormId;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }



    public String getAppendHead() {
        return appendHead;
    }

    public void setAppendHead(String appendHead) {
        this.appendHead = appendHead;
    }

    public String getAppendFoot() {
        return appendFoot;
    }

    public void setAppendFoot(String appendFoot) {
        this.appendFoot = appendFoot;
    }


    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getInitExpress() {
        return initExpress;
    }

    public void setInitExpress(String initExpress) {
        this.initExpress = initExpress;
    }

    public String getSubmitExpress() {
        return submitExpress;
    }

    public void setSubmitExpress(String submitExpress) {
        this.submitExpress = submitExpress;
    }

    public String getTemplateBodyForm() {
        return templateBodyForm;
    }

    public void setTemplateBodyForm(String templateBodyForm) {
        this.templateBodyForm = templateBodyForm;
    }


    public String getGroupData() {
        return groupData;
    }

    public void setGroupData(String groupData) {
        this.groupData = groupData;
    }
}
