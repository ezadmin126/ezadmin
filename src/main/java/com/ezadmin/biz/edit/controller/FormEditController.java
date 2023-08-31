package com.ezadmin.biz.edit.controller;

import com.ezadmin.EzBootstrap;
import com.ezadmin.biz.base.controller.BaseController;
import com.ezadmin.biz.dao.PluginsDao;
import com.ezadmin.biz.emmber.form.DefalutEzFormBuilder;
import com.ezadmin.biz.emmber.form.EzFormBuilder;
import com.ezadmin.biz.emmber.form.EzFormDTO;
import com.ezadmin.biz.list.service.ListService;
import com.ezadmin.common.annotation.EzMapping;
import com.ezadmin.common.utils.*;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @title: FormEditController
 * @Author EzAdmin
 * @Date: 2022/3/9 13:09
 */
@EzMapping("/ezadmin/form/")
public class FormEditController extends BaseController {
    ListService listService = EzProxy.singleInstance(ListService.class);

    @EzMapping("formEdit.html")
    public String formEdit(HttpServletRequest request, HttpServletResponse response) throws Exception {



        String formId = Utils.trimNull(request.getAttribute("FORM_ID"));
        String ENCRYPT_FORM_ID = Utils.trimNull(request.getAttribute("ENCRYPT_FORM_ID"));
        request.setAttribute("formSubmitUrl",request.getContextPath()+"/ezadmin/form/doSubmit-"+ ENCRYPT_FORM_ID);
        request.setAttribute("formUrl",request.getContextPath()+"/ezadmin/form/form-"+ ENCRYPT_FORM_ID);
        Map<String,Object> searchParamsValues=requestToMap(request);
        Map<String, String> sessionMap = sessionToMap(request.getSession());
        searchParamsValues.put("ContextPath", request.getContextPath());

        searchParamsValues.put("LIST_ID", Utils.trimNull(request.getAttribute("LIST_ID")));
        searchParamsValues.put("FORM_ID",formId);
        searchParamsValues.put("ENCRYPT_FORM_ID",ENCRYPT_FORM_ID);

        EzFormBuilder form=new DefalutEzFormBuilder(EzBootstrap.instance().getOriginDataSource(), searchParamsValues,sessionMap);

        if (form == null) {
            return  "404";
        }
        //form.load();
        form.renderHtml();

        String js="<script type='text/javascript'  src='/webjars/ezadmin/plugins/dragula/dragula.min.js'></script>" +
                "<script src='/webjars/ezadmin/js/ez-form-edit-html.js?v="+System.currentTimeMillis()+"'></script>";
        ((EzFormDTO)form.getData()).getForm().setAppendFoot(form.getData().getForm().getAppendFoot()+js);


        request.setAttribute("data",form.getData());
        if(StringUtils.isNotBlank(request.getParameter("_edit"))){
            List<Map<String, Object>> plugins= PluginsDao.getInstance().allFormPlugin();
            request.setAttribute("plugins",plugins);
            return "layui/pages/formedit";
        }
        return "layui/form/form";
     }
    @EzMapping("edit.html")
    public String edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //所有表单类型的插件
        List<Map<String, Object>> plugins= PluginsDao.getInstance().allFormPlugin();
        request.setAttribute("plugins",plugins);
        return "layui/pages/formedit";
    }
}
