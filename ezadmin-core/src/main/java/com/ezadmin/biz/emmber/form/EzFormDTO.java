package com.ezadmin.biz.emmber.form;

import java.util.List;
import java.util.Map;

/**
 *
 **/
public class EzFormDTO {
         private String _DATA_ID_NAME;
    private String formSubmitUrl;
    private String formUrl;
private String id;
    private String validateRules;
    private String validateMessages;
    private EzFormVO form;
   private List<Map<String, String>> formItem;
   private List<Map<String, String>> formNavbars;

   private List<EzGroupFormItemVO> groupFormItem;


    public EzFormVO getForm() {
        return form;
    }

    public void setForm(EzFormVO form) {
        this.form = form;
    }

    public List<Map<String, String>> getFormItem() {
        return formItem;
    }

    public void setFormItem(List<Map<String, String>> formItem) {
        this.formItem = formItem;
    }

    public String get_DATA_ID_NAME() {
        return _DATA_ID_NAME;
    }

    public void set_DATA_ID_NAME(String _DATA_ID_NAME) {
        this._DATA_ID_NAME = _DATA_ID_NAME;
    }

    public String getFormSubmitUrl() {
        return formSubmitUrl;
    }

    public void setFormSubmitUrl(String formSubmitUrl) {
        this.formSubmitUrl = formSubmitUrl;
    }

    public String getFormUrl() {
        return formUrl;
    }

    public void setFormUrl(String formUrl) {
        this.formUrl = formUrl;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getValidateRules() {
        return validateRules;
    }

    public void setValidateRules(String validateRules) {
        this.validateRules = validateRules;
    }

    public String getValidateMessages() {
        return validateMessages;
    }

    public void setValidateMessages(String validateMessages) {
        this.validateMessages = validateMessages;
    }


    public List<Map<String, String>> getFormNavbars() {
        return formNavbars;
    }

    public void setFormNavbars(List<Map<String, String>> formNavbars) {
        this.formNavbars = formNavbars;
    }

    public List<EzGroupFormItemVO> getGroupFormItem() {
        return groupFormItem;
    }

    public void setGroupFormItem(List<EzGroupFormItemVO> groupFormItem) {
        this.groupFormItem = groupFormItem;
    }
}
