package top.ezadmin.config;


import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UrlPathHelper;
import top.ezadmin.EzClientProperties;
import top.ezadmin.blog.constants.Constants;
import top.ezadmin.blog.constants.Nologin;
import top.ezadmin.blog.model.User;
import top.ezadmin.blog.utils.DoCookie;
import top.ezadmin.common.constants.SessionConstants;
import top.ezadmin.common.utils.*;
import top.ezadmin.domain.mapper.SysUserMapper;
import top.ezadmin.domain.mapper.ext.SysUserExtMapper;
import top.ezadmin.domain.model.SysUser;
import top.ezadmin.web.EzResult;
import top.ezadmin.web.SpringContextHolder;
import top.ezadmin.web.safe.IpActionDto;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

/**
 *
 **/
 public class UserAccessFilter extends OncePerRequestFilter {
    private final Logger LOG = LoggerFactory.getLogger(UserAccessFilter.class);

    AntPathMatcher matcher=new AntPathMatcher();
    @Resource
    private HandlerMapping handlerMapping;

    @Resource
    EzClientProperties properties;
    @Resource
    SysUserMapper sysUserMapper;
    @Resource
    SysUserExtMapper sysUserExtMapper;


    private List<String> staticUrl=new ArrayList<>();
    private List<String> excludeUrl=new ArrayList<>();
    Long vi=System.currentTimeMillis();

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        UrlPathHelper helper = new UrlPathHelper();
        String realUrl = helper.getRequestUri(httpServletRequest);
        //静态请求
        try {

            if (staticUrl(realUrl,httpServletRequest)  ) {
                httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
                httpServletResponse.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            httpServletResponse.getWriter().println("500 contact admin");
            return;
        }
        String ip=IpUtils.getRealIp(httpServletRequest);
        String p= JSONUtils.toJSONString(requestToMap(httpServletRequest));
        IpActionDto ipActionDto = new IpActionDto();
        ipActionDto.setIp(ip );
        ipActionDto.setUri(realUrl);
        ipActionDto.setP(p);
        if( !realUrl.startsWith("/camunda")&& !realUrl.startsWith("/ezadmin")&&!realUrl.startsWith("/topezadmin") &&!top.ezadmin.web.safe.DefaultLocalFilter.isSafe(ipActionDto)){
            logger.error("拦截攻击 429");
            httpServletResponse.setStatus(429);
            httpServletResponse.getWriter().println("429 Too Many Requests");
            return;
        }

        httpServletRequest.setAttribute("downloadUrl", SpringContextHolder.getBean(EzClientProperties.class).getDownloadUrl());
        httpServletRequest.setAttribute("vi", vi);
        httpServletRequest.getSession().setAttribute("downloadUrl",SpringContextHolder.getBean(EzClientProperties.class).getDownloadUrl());
        httpServletRequest.setAttribute("companyName","");
        User user = (User) httpServletRequest.getSession().getAttribute(SessionConstants.EZ_SESSION_USER_KEY);
        //cookie跟踪
        DoCookie cookie=new DoCookie();
        String idName=  cookie.getDecodeCookie(Constants.EZ_SID);
        String[] idNameArray = StringUtils.split(idName, "@@");
        //
        try {
            if(noLogin(httpServletRequest,realUrl)){
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return  ;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(idNameArray==null||idNameArray.length!=2){
            httpServletResponse.sendRedirect("/login/login.html");
            return;
        }

//        //判断sid是否正常
         SysUser dbUser= sysUserMapper.selectByPrimaryKey(NumberUtils.toLong(idNameArray[0]));

//
        if(dbUser==null){
            httpServletResponse.sendRedirect("/login/login.html");
            return;
        }
//        //从cookie里面获取
        if(user==null) {
            user = new User();
            user.setUserId(dbUser.getUserId());
            user.setUserName(dbUser.getUserName());
            user.setCompanyId(dbUser.getCompanyId());
            user.setRoleNames(sysUserExtMapper.selectUserRoles(dbUser.getUserId()));
            user.setResourceIds(sysUserExtMapper.selectUserResources(dbUser.getUserId()));
        }
        httpServletRequest.getSession().setAttribute(SessionConstants.EZ_SESSION_USER_KEY, user);
        httpServletRequest.getSession().setAttribute(SessionConstants.EZ_SESSION_USER_NAME_KEY, user.getUserName());
        httpServletRequest.getSession().setAttribute(SessionConstants.EZ_SESSION_USER_ID_KEY, user.getUserId());
        httpServletRequest.getSession().setAttribute(SessionConstants.EZ_SESSION_COMPANY_ID_KEY, user.getCompanyId());
        httpServletRequest.setAttribute(SessionConstants.EZ_SESSION_USER_NAME_KEY, user.getUserName());
        httpServletRequest.setAttribute("resetPasswordUrl", "/login/resetPassword.html?userId=" + user.getUserId());
        httpServletRequest.setAttribute(SessionConstants.EZ_SESSION_USER_ID_KEY, user.getUserId());
        httpServletRequest.setAttribute("EZ_SESSION_USER_ORG_KEY", user.getOrgName());
        //
        Map<String,String> sesion_ez=new HashMap<>();
        sesion_ez.put("COMPANY_ID", user.getCompanyId()+"");
        httpServletRequest.getSession().setAttribute(SessionConstants.EZ_SESSION_PARAM_KEY,sesion_ez);
//

        Map<String,Object> param = new HashMap<>();
        long start=System.currentTimeMillis();
        try {

            Utils.initRequestUrl(realUrl);
            boolean au=auth(realUrl,httpServletRequest, httpServletResponse,filterChain);
            if(au) {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                param =requestToMap(httpServletRequest);
                if( realUrl.startsWith("/topezadmin")){
                   // doEzadminBefore(httpServletRequest,user);
                }
            }else{
                LOG.warn("auth2 null {} {}",realUrl,user.getResourceIds());
                // httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
                EzResult.instance().fail().code(HttpStatus.FORBIDDEN.value()+"").message("没有权限").printJSONUtils(httpServletResponse);
                // httpServletResponse.getWriter().println("403 contact admin");
               // httpServletResponse.sendRedirect("/error/403.html?filter=1");
            }
        } finally {
            Utils.clearLog();
            if(!realUrl.startsWith("/message")){
                LOG.info("{} end：{}ms \t{}\t{}-{}\t{}" ,IpUtils.getRealIp(httpServletRequest),(System.currentTimeMillis() - start),  httpServletRequest.getRequestURL(),
                        param.size(), param
                );
            }
        }



    }

    private boolean staticUrl(String realUrl,HttpServletRequest httpServletRequest) throws Exception {
        for (int i = 0; i < staticUrl.size(); i++) {
            if(matcher.match(staticUrl.get(i),realUrl)){
                return true;
            }
        }

        return false;
    }
        private boolean noLogin(HttpServletRequest httpServletRequest,String url) throws Exception {
            if( !url.startsWith("/topezadmin")&&!url.startsWith("/ezadmin")) {
                HandlerExecutionChain handlerExecutionChain = handlerMapping.getHandler(httpServletRequest);
                if(handlerExecutionChain==null){
                   // logger.warn("handlerExecutionChain null {}"+url);
                    return true;
                }
                Object handler = handlerExecutionChain.getHandler();
                if (handler!=null&& handler instanceof HandlerMethod) {
                    HandlerMethod handlerMethod = (HandlerMethod) handler;
                    Method method = handlerMethod.getMethod();
                    Class<?> controllerClass = method.getDeclaringClass();
                    Annotation annotation = controllerClass.getAnnotation(Nologin.class);
                    Annotation m = method.getAnnotation(Nologin.class);
                    if (annotation != null || m != null) {
                        // 处理注解逻辑
                        return true;
                    }
                }
            }

            return false;
     }


    private boolean auth(String url,HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) {
        try {
            for (int i = 0; i < excludeUrl.size(); i++) {
                if (matcher.match(excludeUrl.get(i),url)) {
                    return true;
                }
            }
            User user = (User) httpServletRequest.getSession().getAttribute("EZ_SESSION_USER_KEY");
            UrlPathHelper helper = new UrlPathHelper();
            String realUrl = helper.getRequestUri(httpServletRequest);
            if(user.getRoleNames().contains("管理员")){
                return  true;
            }

            for (int i = 0; i < user.getResourceIds().size(); i++) {
                if(matcher.match(user.getResourceIds().get(i),realUrl)){
                    return true;
                }
            }
            LOG.warn("auth2 null {} {}",realUrl,user.getResourceIds());
        } catch (Exception e) {
            // 异常处理逻辑
            LOG.error("",e);
        }
        return false;
    }



    protected Map<String,Object> requestToMap(HttpServletRequest request){
        Map<String, Object> searchParamsValues = new HashMap<>();
        try{
            request.getParameterMap().forEach((k, v) -> {
                if(v==null){
                    return;
                }
                if(v!=null&&v.length==1){
                    searchParamsValues.put(k, v[0]);
                }else if(v.length>1){
                    searchParamsValues.put(k+"_ARRAY",request.getParameterValues(k));
                }
            });}catch (Exception e){}
        return searchParamsValues;
    }


    @Override
    public void initFilterBean() throws ServletException {
        top.ezadmin.web.safe.DefaultLocalFilter.init();
    }


    public List<String> getStaticUrl() {
        return staticUrl;
    }

    public void setStaticUrl(List<String> staticUrl) {
        this.staticUrl = staticUrl;
    }

    public List<String> getExcludeUrl() {
        return excludeUrl;
    }

    public void setExcludeUrl(List<String> excludeUrl) {
        this.excludeUrl = excludeUrl;
    }

}
