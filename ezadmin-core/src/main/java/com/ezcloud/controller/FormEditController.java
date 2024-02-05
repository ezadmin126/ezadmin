package com.ezcloud.controller;

import com.ezcloud.dao.FormDao;
import com.ezcloud.dao.PluginsDao;
import com.ezcloud.service.FormService;
import com.ezcloud.common.annotation.EzMapping;
import com.ezcloud.common.utils.*;
import com.ezcloud.web.EzResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title: FormEditController
 * @Author EzAdmin
 * @Date: 2022/3/9 13:09
 */
@EzMapping("/ezcloud/form/")
public class FormEditController extends BaseController {

    FormService formService = EzProxy.singleInstance(FormService.class);

    @EzMapping("submitEdit.html")
    public EzResult edit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //所有表单类型的插件
        System.out.println(request.getParameter("data"));
        Map<String, Object> form=JSONUtils.parseObjectMap(request.getParameter("data"));

        String html=  FormDao.getInstance().updateForm(form);

        if(StringUtils.isNotBlank(html)){
            return EzResult.instance().data("html",html);
        }
        return EzResult.instance().fail();
    }
    @EzMapping("loadEdit.html")
    public String loadEdit(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String,Object> searchParamsValues=requestToMap(request);

        Map<String, String> sessionMap = sessionToMap(request.getSession());

        String ENCRYPT_FORM_ID = Utils.trimNull(request.getAttribute("ENCRYPT_FORM_ID"));

        List<Map<String, Object>> plugins= PluginsDao.getInstance().allFormPlugin();
        request.setAttribute("plugins",plugins);
        Map<String, Object> form=new HashMap<>();
        if(StringUtils.isNotBlank(ENCRYPT_FORM_ID)){
            form=   JSONUtils.parseObjectMap(formService.selectAllFormById(ENCRYPT_FORM_ID))  ;
        }else{
            form=new HashMap();
            form.put("core",new HashMap());
        }
        Map<String,Object> core=(Map<String,Object>)form.get("core");
        core.put(JsoupUtil.ADMINSTYLE,"layui");
        formService.fillFormById(form,searchParamsValues,sessionMap);
        request.setAttribute("form",form);
        return "layui/formedit";
    }



}
