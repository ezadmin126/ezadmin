package top.ezadmin.controller;

import com.alibaba.fastjson.JSON;
import top.ezadmin.common.NotExistException;
import top.ezadmin.common.annotation.EzMapping;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.cache.CacheNameSpace;
import top.ezadmin.plugins.cache.Callback;
import top.ezadmin.web.EzResult;
import top.ezadmin.EzClientBootstrap;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@EzMapping("/topezadmin/api/")
public class EzApiController extends BaseController {
    Logger log = LoggerFactory.getLogger(EzApiController.class);

    EzClientBootstrap bootstrap= EzClientBootstrap.instance();
    @EzMapping("region.html")
    public void list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url="";
        Map<String,String> searchParamsValues=new HashMap<>();
        final String level= StringUtils.isBlank(request.getParameter("level"))?"3":request.getParameter("level");
        EzResult ezResult=(EzResult) EzClientBootstrap.instance().getCache().get60(CacheNameSpace.REGION.name()+level,
                bootstrap.getRegionUrl(), new Callback() {
                @Override
                public Object call(String key) {
                    try {
                        String jsonStr=  Utils.postBody2WithTimeOut(bootstrap.getRegionUrl()+"&level="+level,JSON.toJSONString(new HashMap<String,String>()));
                        return EzResult.instance().data(JSON.parseObject(jsonStr).get("listData"));
                    } catch (Exception e) {
                        Utils.addLog("",e);
                        return EzResult.instance().fail().msg("", ExceptionUtils.getFullStackTrace(e));
                    }
                }
             })  ;
        if(ezResult==null){
            ezResult=EzResult.instance().msg("error","error");
        }
        ezResult.printJSONUtils( response);
    }
    @EzMapping("org.html")
    public void org(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url="";

       final String level=StringUtils.isBlank(request.getParameter("level"))?"3":request.getParameter("level");
        final String type=StringUtils.isBlank(request.getParameter("type"))?"":request.getParameter("type");
        final String parentId=StringUtils.isBlank(request.getParameter("parentId"))?"2":request.getParameter("parentId");
        EzResult ezResult=(EzResult) EzClientBootstrap.instance().getCache().get60(CacheNameSpace.ORG.name()+level+"_"+type,
                bootstrap.getOrgUrl(), new Callback() {
                    @Override
                    public Object call(String key) {
                        String jsonStr="";
                        try {
                            String orgUrl=bootstrap.getOrgUrl();
                            if(orgUrl.indexOf("?")<=0){
                                orgUrl=orgUrl+"?1=1";
                            }
                              jsonStr=  Utils.postBody2WithTimeOut(orgUrl+"&parentId="+parentId+"&level="+level+"&type="+type,JSON.toJSONString(new HashMap<String,String>()),30000);
                            return EzResult.instance().data(JSON.parseObject(jsonStr).get("listData"));
                        } catch (Exception e) {
                            Utils.addLog("",e);
                            return EzResult.instance().fail().msg("", jsonStr+"###"+ExceptionUtils.getFullStackTrace(e));
                        }
                    }

                });
        if(ezResult==null){
            ezResult=EzResult.instance().msg("error","error");
        }
        ezResult.printJSONUtils( response);
    }
    @EzMapping("category.html")
    public void category(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String url="";
        Map<String,String> searchParamsValues=new HashMap<>();
        for(Map.Entry<String, String[]> e:request.getParameterMap().entrySet()){
            searchParamsValues.put(e.getKey(),StringUtils.trimEmpty(StringUtils.join(e.getValue())));
        }
        EzResult ezResult=null;
        final String searchParamJson=JSON.toJSONString(searchParamsValues);
        if(StringUtils.equals(request.getParameter("isCache"),"0")){
            String jsonStr=  Utils.postBody2WithTimeOut(bootstrap.getCategoryUrl(),searchParamJson,30000);
            ezResult= EzResult.instance().data(JSON.parseObject(jsonStr).get("listData"));
        }else {
            ezResult = (EzResult) EzClientBootstrap.instance().getCache().get60(CacheNameSpace.CATEGORY.name(),
                    bootstrap.getCategoryUrl(), new Callback() {
                        @Override
                        public Object call(String key) {
                            try {
                                String jsonStr = Utils.postBody2WithTimeOut(bootstrap.getCategoryUrl(),searchParamJson, 30000);

                                return EzResult.instance().data(JSON.parseObject(jsonStr).get("listData"));
                            } catch (Exception e) {
                                Utils.addLog("", e);
                            }
                            return "500";
                        }


                    });
        }
        ezResult.printJSONUtils(response);
    }

    @EzMapping("datagroup.html")
    public void datagroup(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String listUrlCode = Utils.trimNull(request.getAttribute("ENCRYPT_ID"));
        if(StringUtils.isBlank(listUrlCode)){
            throw new NotExistException();
        }
        EzResult.instance().printJSONUtils(response);
    }
}
