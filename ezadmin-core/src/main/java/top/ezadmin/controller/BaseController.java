package top.ezadmin.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.common.constants.SessionConstants;
import top.ezadmin.common.enums.DefaultParamEnum;
import top.ezadmin.common.utils.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class BaseController {
    public Logger logger = LoggerFactory.getLogger(BaseController.class);




    public String getIdInForm(HttpServletRequest request) throws Exception {
        String _DATA_ID_NAME=request.getParameter("_DATA_ID_NAME");
        if(StringUtils.isBlank(_DATA_ID_NAME)){
            _DATA_ID_NAME="ID";
        }
        String originId = request.getParameter(_DATA_ID_NAME);
        String id=originId;
        if(_DATA_ID_NAME.startsWith("ENCODE_")){
            id= DESUtils.decryptDES(originId);
        }
        if(StringUtils.isJsBlank(id)){
            id=null;
        }
        return id;
    }
    public String toFormId(Object rowId,HttpServletRequest request) throws Exception {
        String _DATA_ID_NAME=request.getParameter("_DATA_ID_NAME");
        if(StringUtils.isBlank(_DATA_ID_NAME)){
            _DATA_ID_NAME="ID";
        }
        String originId = request.getParameter(_DATA_ID_NAME);
        if(_DATA_ID_NAME.startsWith("ENCODE_")){
            originId=DESUtils.encryptDES(rowId+"");
        }
        if(StringUtils.isBlank(originId)){
            originId= Utils.trimNull(rowId);
        }
        return originId;
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
            if(v.length==1){
                searchParamsValues.put(k, v[0]==null?null:v[0].trim());
                if(StringUtils.equalsIgnoreCase(DefaultParamEnum.EZ_TODAY_DATE.name(),v[0])){
                    searchParamsValues.put(k,DefaultParamEnum.getValue(DefaultParamEnum.EZ_TODAY_DATE.name()));
                }
                if(StringUtils.equalsIgnoreCase(DefaultParamEnum.EZ_TODAY_DATE_NORMAL.name(),v[0])){
                    searchParamsValues.put(k,DefaultParamEnum.getValue(DefaultParamEnum.EZ_TODAY_DATE_NORMAL.name()));
                }
                if(StringUtils.equalsIgnoreCase(DefaultParamEnum.EZ_CURRENT_MONTH.name(),v[0])){
                    searchParamsValues.put(k,DefaultParamEnum.getValue(DefaultParamEnum.EZ_CURRENT_MONTH.name()));
                }
                if(StringUtils.equalsIgnoreCase(SessionConstants.EZ_SESSION_USER_ID_KEY,v[0])){
                    searchParamsValues.put(k,Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_ID_KEY)));
                }
                searchParamsValues.put(k+"_ARRAY",v);
            }else if(v.length>1){
                searchParamsValues.put(k+"_ARRAY",request.getParameterValues(k));
                searchParamsValues.put(k, JSONUtils.toJSONString(request.getParameterValues(k)));
            }
        }
        searchParamsValues.put("EZ_SESSION_USER_ID_KEY",Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_ID_KEY)));
        searchParamsValues.put("EZ_SESSION_USER_NAME_KEY",Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_NAME_KEY)));
        searchParamsValues.put("EZ_CLIENT_IP", IpUtils.getRealIp(request));
        searchParamsValues.put("EZ_REFERER", request.getHeader("referer"));
        searchParamsValues.put("HttpServletRequest", request);
        searchParamsValues.put("COMPANY_ID",Utils.trimNull(request.getSession().getAttribute(SessionConstants.EZ_SESSION_COMPANY_ID_KEY)));
        return searchParamsValues;
    }

    public Map<String, String> sessionToMap(HttpSession session) {
        Map<String, String> map =new HashMap<>();
        try {
            Object sessionParam = session.getAttribute(SessionConstants.EZ_SESSION_PARAM_KEY);
            if(sessionParam!=null&&sessionParam instanceof Map){
                map = (Map<String, String>) sessionParam;
            }
        }catch (Exception e){
            logger.error("",e);
        }
        map.put("EZ_SESSION_USER_ID_KEY",Utils.trimNull(session.getAttribute(SessionConstants.EZ_SESSION_USER_ID_KEY)));
        map.put("EZ_SESSION_USER_NAME_KEY",Utils.trimNull(session.getAttribute(SessionConstants.EZ_SESSION_USER_NAME_KEY)));
        map.put("EZ_SESSION_COMPANY_ID_KEY",Utils.trimNull(session.getAttribute(SessionConstants.EZ_SESSION_COMPANY_ID_KEY)));
        map.put("COMPANY_ID",Utils.trimNull(session.getAttribute(SessionConstants.EZ_SESSION_COMPANY_ID_KEY)));
        return map;
    }
}
