package io.github.ezadmin126.controller;

import io.github.ezadmin126.EzBootstrap;
import io.github.ezadmin126.web.EzResult;
import io.github.ezadmin126.web.RequestContext;

import java.util.Map;

public class BaseController {

    public EzResult render(String path,Map<String,Object> templateParam) throws Exception {
        templateParam.put("vi","303");
        templateParam.put("downloadUrl",EzBootstrap.config().getDownloadUrl());
        templateParam.put("uploadUrl",EzBootstrap.config().getUploadUrl());
        templateParam.putAll(EzBootstrap.config().getConfig());
        templateParam.put("layout",EzBootstrap.config().getLayout());
        templateParam.put("logoUrl",EzBootstrap.config().getLogoUrl());
        templateParam.put("prefixUrl",EzBootstrap.config().getPrefixUrl());
        templateParam.put("systemName",EzBootstrap.config().getSystemName());
        templateParam.put("appName",EzBootstrap.config().getAppName());
        templateParam.put("signoutUrl",EzBootstrap.config().getSignoutUrl());
       return EzResult.instance().data("html",EzBootstrap.config().getEzTemplate().renderFile(path,templateParam))
               .code("HTML");
    }
    // 保留原有方法以保持兼容性
    public String getIdInForm(RequestContext requestContext) throws Exception {
        return requestContext.getParameter("ID")+"";
    }
    
    // 辅助方法：将RequestContext转换为Map
    public Map<String, Object> requestToMap(RequestContext requestContext) {
        return requestContext.getRequestParams();
    }
    
    // 辅助方法：将RequestContext的session转换为Map
    public Map<String, String> sessionToMap(RequestContext requestContext) {
        return requestContext.getSessionParams();
    }
    
    // 辅助方法：将rowId转换为表单ID
    public String toFormId(Object rowId, RequestContext requestContext) {
        return rowId != null ? rowId.toString() : "";
    }
}
