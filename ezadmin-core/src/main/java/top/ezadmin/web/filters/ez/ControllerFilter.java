package top.ezadmin.web.filters.ez;

 import top.ezadmin.common.NotExistException;
 import top.ezadmin.common.annotation.EzMapping;
 import top.ezadmin.EzClientBootstrap;
 import top.ezadmin.common.utils.*;
 import top.ezadmin.web.EzResult;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 import org.thymeleaf.context.WebContext;

 import javax.servlet.FilterChain;
 import javax.servlet.ServletException;
 import javax.servlet.http.Cookie;
 import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControllerFilter extends Filter {
    Logger logger = LoggerFactory.getLogger(ControllerFilter.class);
    public static String includeShow="/topezadmin/(list|form|listEdit|formEdit|detail)/([A-Za-z]+)-(.*)";
    Map<String, Map<String, Object>> REQUEST_MAPPING = new HashMap<String, Map<String, Object>> ();
    String pack = "top.ezadmin.controller";
    public static String vesion=System.currentTimeMillis()+"";

    Pattern pInclude = Pattern.compile(includeShow);

    public ControllerFilter()  {
        super.initFilterBean();
        Set<String> controllersNew = ClassUtils.loadAllClassByPackage(pack );
        for (String item:controllersNew) {
            try {
                if (item.endsWith("Controller")) {
                    Object bean = BeanTools.applicationInstance(item);
                    processEzMapping(bean);
                }
            } catch (Exception e) {
                Utils.addLog(item, e);
            }
        }
    }

    private void processEzMapping(Object bean) {
        EzMapping conAnno = bean.getClass().getAnnotation(EzMapping.class);
        String controllerUrl = "";
        if (conAnno != null) {
            controllerUrl = conAnno.value();
        }
        Method[] method = bean.getClass().getDeclaredMethods();
        for (int i1 = 0; i1 < method.length; i1++) {
            EzMapping mAnno = method[i1].getAnnotation(EzMapping.class);
            if (mAnno == null) {
                continue;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("controller", bean);
            map.put("method", method[i1]);
            REQUEST_MAPPING.put(controllerUrl + mAnno.value(), map);
        }
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
       String originatingUrl =   request.getRequestURI() ;
        originatingUrl= originatingUrl.replace("/ezadmin/","/topezadmin/")
                .replace("/ezcloud/","/topezadmin/");
        originatingUrl = originatingUrl.replaceAll("\\\\", "\\");
        int js=originatingUrl.indexOf(";");
        if(js>=0) {
            originatingUrl = originatingUrl.substring(0, js);
        }
        String contextName = request.getServletContext().getContextPath();
        request.setAttribute("contextName", contextName);
        request.setAttribute("holiday", EzClientBootstrap.instance().getHoliday());
        request.setAttribute("vi", vesion);

        Cookie[] cookie=request.getCookies();
        if(cookie!=null){
            for (int i = 0; i < cookie.length; i++) {
                if(cookie[i].getName().equals("layui-theme-mode-prefer-dark")){
                    try {
                        request.setAttribute("darkTheme",cookie[i].getValue());
                    } catch (Exception e) {

                    }
                }
            }
        }


        Matcher m=pInclude.matcher(originatingUrl);
        if(m.find()&&m.groupCount()==3){
             String contro=m.group(1);
             String method=m.group(2);
             String id=m.group(3);
             if(id.endsWith("\\")){
                id=id.substring(0,id.length()-2);
             }
             try {
                 originatingUrl = "/topezadmin/" + contro + "/" + method+".html";
                 switch (contro) {
                     case "authc":
                     case "anon":
                     case "list":
                     case "listEdit":
                         request.setAttribute("ENCRYPT_LIST_ID", id);
                         break;
                     case "form":
                     case "formEdit":
                         request.setAttribute("ENCRYPT_FORM_ID", id);
                         break;
                 }
             }catch (IllegalArgumentException  ee){
                 //ignor
                 logger.warn("",ee);
             }
             catch (Exception e){
                 logger.error("URL 错误：{}",originatingUrl,e);
             }
        }
        if (REQUEST_MAPPING.containsKey(originatingUrl)) {
            request.setAttribute("contextName", contextName);
            request.setAttribute("uploadUrl",  request.getContextPath()+ EzClientBootstrap.instance().getUploadUrl());
            if(StringUtils.startsWith(EzClientBootstrap.instance().getDownloadUrl(),"http")){
                request.setAttribute("downloadUrl", EzClientBootstrap.instance().getDownloadUrl());
            }else{
                request.setAttribute("downloadUrl",request.getContextPath()+ EzClientBootstrap.instance().getDownloadUrl());
            }

            request.setAttribute("adminStyle", EzClientBootstrap.instance().getAdminStyle());

            Map<String, Object> map = REQUEST_MAPPING.get(originatingUrl);
            Object controller =  map.get("controller");
            Method method = (Method) map.get("method");
            try {
                if (method.getReturnType().equals(Void.TYPE)) {
                    method.invoke(controller, request, response);
                } else if (method.getReturnType().equals(String.class)) {
                    String view = method.invoke(controller, request, response) + "";
                    if(StringUtils.startsWith(view,"redirect:")){
                        view=StringUtils.replace(view,"redirect:","");
                        response.sendRedirect(view);
                    }else {
                        view(view, request, response);
                    }
                } else if (method.getReturnType().equals(EzResult.class)) {
                    EzResult result = (EzResult) method.invoke(controller, request, response);
                    result.printJSONUtils(response);
                } else {
                    method.invoke(controller, request, response);
                }
            }catch (InvocationTargetException vo){
                if(vo.getTargetException() instanceof NotExistException){
                    filterChain.doFilter(request,response);
                    return;
                }
                logger.error("找不到指定的方法",vo.getTargetException());
                EzResult.instance().msg("500", ExceptionUtils.getFullStackTrace(vo.getTargetException())).printJSONUtils(response);
            }
            catch (Exception e) {
                logger.error("找不到指定的方法",e);
                EzResult.instance().msg("500", ExceptionUtils.getFullStackTrace(e)).printJSONUtils(response);
            }
            return;
        }
        getNext().doFilter(request, response,filterChain);
    }

    protected void view(String view, HttpServletRequest request, HttpServletResponse response) {
        WebContext context = new WebContext(request, response, request.getServletContext());
        try {
            ThymeleafUtils.process(view, context, response.getWriter());
        } catch (IOException e) {
            Utils.addLog("渲染页面失败"+view+ request.getRequestURI(),e);
        }
    }
}
