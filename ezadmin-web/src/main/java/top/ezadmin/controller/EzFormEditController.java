package top.ezadmin.controller;

import top.ezadmin.common.utils.*;
import top.ezadmin.dao.FormDao;
import top.ezadmin.dao.PluginsDao;
import top.ezadmin.service.FormService;
import top.ezadmin.web.EzResult;
import top.ezadmin.domain.mapper.EzCloudMapper;
import top.ezadmin.domain.model.EzCloud;
import top.ezadmin.domain.model.EzCloudExample;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ezform/")
public class EzFormEditController extends BaseController {

    FormService formService = EzProxy.singleInstance(FormService.class);
    @Resource
    EzCloudMapper ezCloudMapper;
    @RequestMapping("submitEdit.html")
    @ResponseBody
    public EzResult edit(Long cloudId,HttpServletRequest request, HttpServletResponse response) throws Exception {
        //所有表单类型的插件
        System.out.println(request.getParameter("data"));
        Map<String, Object> form= JSONUtils.parseObjectMap(request.getParameter("data"));

        String html=  FormDao.getInstance().updateForm(form);

        if(StringUtils.isNotBlank(html)){
            Map<String,Object> coreMap=(Map<String,Object>)form.get("core");


            String formCode= Utils.trimNull( coreMap.get("formcode"));
            String formName=  Utils.trimNull( coreMap.get(JsoupUtil.FORM_NAME.toLowerCase()));
            String datasource=Utils.trimNull( coreMap.get(JsoupUtil.DATASOURCE));

            EzCloud record=new EzCloud();
            record.setEzCode(formCode);
            record.setUpdateTime(new Date());
            record.setEzName(formName);
            record.setEzConfig(html);
            record.setDatasource(datasource);
            record.setUpdateName(IpUtils.getRealIp(request));
            if(cloudId!=null){
                record.setId(cloudId);
                ezCloudMapper.updateByPrimaryKeySelective(record);
            }else{
                record.setIsDel(0);
                record.setEzType(2);
                record.setAddTime(new Date());
                ezCloudMapper.insert(record);
                cloudId=record.getId();
            }
            return EzResult.instance().data("cloudId",cloudId+"");
        }
        return EzResult.instance().fail();
    }
    @RequestMapping("loadEdit.html")
    public String loadEdit(Long cloudId, String ENCRYPT_FORM_ID, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        model.addAttribute("vi",System.currentTimeMillis());

        Map<String,Object> searchParamsValues=requestToMap(request);

        Map<String, String> sessionMap = sessionToMap(request.getSession());

        Map<String, Object> form=new HashMap<>();
        List<Map<String, Object>> plugins= PluginsDao.getInstance().allFormPlugin();
        request.setAttribute("plugins",plugins);
        if(cloudId!=null){
            EzCloud ezCore=ezCloudMapper.selectByPrimaryKey(cloudId);
            if(ezCore!=null){
                form= formService.selectAllFormByHtml(ezCore.getEzConfig(),"layui")  ;
            }
        }else if(StringUtils.isNotBlank(ENCRYPT_FORM_ID)){
            EzCloudExample example=new EzCloudExample();
            example.createCriteria().andEzCodeEqualTo(ENCRYPT_FORM_ID).andEzTypeEqualTo(2).andIsDelEqualTo(0);
            example.setOrderByClause("  ID desc");
            List<EzCloud>  listDbList=ezCloudMapper.selectByExampleWithBLOBs(example);
            if(!CollectionUtils.isEmpty(listDbList)){
                form= formService.selectAllFormByHtml(listDbList.get(0).getEzConfig(),"layui")  ;
                cloudId=listDbList.get(0).getId();
            }
        }

        if(form==null||form.isEmpty()){
            form=new HashMap();
            form.put("core",new HashMap());
        }
        Map<String,Object> core=(Map<String,Object>)form.get("core");
        core.put(JsoupUtil.ADMINSTYLE,"layui");
        formService.fillFormById(form,searchParamsValues,sessionMap);
        model.addAttribute("form",form);
        model.addAttribute("cloudId",cloudId);
        return "layui/formedit";
    }

}
