package top.ezadmin.controller;

import top.ezadmin.EzBootstrap;
import top.ezadmin.web.EzResult;
import top.ezadmin.web.RequestContext;

import java.util.Map;

public class BaseController {

    public EzResult render(String path,Map<String,Object> templateParam) throws Exception {
        templateParam.put("vi","303");
        templateParam.putIfAbsent("downloadUrl",EzBootstrap.config().getDownloadUrl());
        templateParam.putIfAbsent("uploadUrl",EzBootstrap.config().getUploadUrl());
        templateParam.putAll(EzBootstrap.config().getConfig());
        templateParam.putIfAbsent("layout",EzBootstrap.config().getLayout());
        templateParam.putIfAbsent("logoUrl",EzBootstrap.config().getLogoUrl());
        templateParam.putIfAbsent("prefixUrl",EzBootstrap.config().getPrefixUrl());
        templateParam.putIfAbsent("systemName",EzBootstrap.config().getSystemName());
        templateParam.putIfAbsent("appName",EzBootstrap.config().getAppName());
        templateParam.putIfAbsent("signoutUrl",EzBootstrap.config().getSignoutUrl());
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
