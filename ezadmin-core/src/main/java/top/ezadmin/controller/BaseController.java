package top.ezadmin.controller;



import top.ezadmin.common.constants.SessionConstants;
import top.ezadmin.common.enums.DefaultParamEnum;
import top.ezadmin.common.utils.DESUtils;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.web.EzResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import java.util.*;

public class BaseController {
    public Logger logger = LoggerFactory.getLogger(ExportController.class);


    protected  boolean checkEditAuth(){
        return  true;//StringUtils.equalsIgnoreCase(EzBootstrap.instance().getAppName(),"ez");
    }

    protected void notFound(boolean ajax, HttpServletRequest request, HttpServletResponse response) {
        try {
              EzResult.instance().code("404").printJSONUtils(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getIdInForm(HttpServletRequest request) throws Exception {
        String _DATA_ID_NAME=request.getParameter("_DATA_ID_NAME");
        if(StringUtils.isBlank(_DATA_ID_NAME)){
            _DATA_ID_NAME="ID";
        }
        String ORIGN_ID = request.getParameter(_DATA_ID_NAME);
        String ID=ORIGN_ID;
        if(_DATA_ID_NAME.startsWith("ENCODE_")){
            ID= DESUtils.decryptDES(ORIGN_ID);
        }
        if(StringUtils.isJsBlank(ID)){
            ID=null;
        }
        return ID;
    }
    public String toFormId(Object rowId,HttpServletRequest request) throws Exception {
        String _DATA_ID_NAME=request.getParameter("_DATA_ID_NAME");
        if(StringUtils.isBlank(_DATA_ID_NAME)){
            _DATA_ID_NAME="ID";
        }
        String ORIGN_ID = request.getParameter(_DATA_ID_NAME);
        if(_DATA_ID_NAME.startsWith("ENCODE_")){
            ORIGN_ID=DESUtils.encryptDES(rowId+"");
        }
        if(StringUtils.isBlank(ORIGN_ID)){
            ORIGN_ID= Utils.trimNull(rowId);
        }
        return ORIGN_ID;
    }


    //同
    public static Map<String,Object> requestToMap( HttpServletRequest request){
        Map<String, Object> searchParamsValues = new HashMap<>();
        for (Map.Entry<String, String[]> entry:request.getParameterMap().entrySet()) {
            String[] v=entry.getValue();
            String k=entry.getKey();
            if(v==null){
                continue;
            }
            if(v!=null&&v.length==1){
                searchParamsValues.put(k, v[0]);
                if(StringUtils.equalsIgnoreCase(DefaultParamEnum.EZ_TODAY_DATE.name(),v[0])){
                    searchParamsValues.put(k,DefaultParamEnum.getValue(DefaultParamEnum.EZ_TODAY_DATE.name()));
                }
                if(StringUtils.equalsIgnoreCase(DefaultParamEnum.EZ_TODAY_DATE_NORMAL.name(),v[0])){
                    searchParamsValues.put(k,DefaultParamEnum.getValue(DefaultParamEnum.EZ_TODAY_DATE_NORMAL.name()));
                }
                if(StringUtils.equalsIgnoreCase(SessionConstants.EZ_SESSION_USER_ID_KEY,v[0])){
                    searchParamsValues.put(k,Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_ID_KEY)));
                }
                searchParamsValues.put(k+"_ARRAY",v);
            }else if(v.length>1){
                searchParamsValues.put(k+"_ARRAY",request.getParameterValues(k));
            }
        }
        searchParamsValues.put("EZ_SESSION_USER_ID_KEY",Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_ID_KEY)));
        searchParamsValues.put("EZ_SESSION_USER_NAME_KEY",Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_NAME_KEY)));

        return searchParamsValues;
    }

    public Map<String, String> sessionToMap(HttpSession session) {
        try {
            Map<String, String> map = (Map<String, String>) session.getAttribute(SessionConstants.EZ_SESSION_PARAM_KEY);
            if (map==null||map.isEmpty()) {
                map =  new java.util.HashMap<>();
            }
            map.put("EZ_SESSION_USER_ID_KEY",Utils.trimNull(session.getAttribute(SessionConstants.EZ_SESSION_USER_ID_KEY)));
            map.put("EZ_SESSION_USER_NAME_KEY",Utils.trimNull(session.getAttribute(SessionConstants.EZ_SESSION_USER_NAME_KEY)));
            return map;
        }catch (Exception e){
            return  Collections.emptyMap();
        }
    }
    protected void print(Object json,HttpServletResponse response) throws IOException {

        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().print(Utils.trimNull(json));
        response.getWriter().flush();
    }

}
