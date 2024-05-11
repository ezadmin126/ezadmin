package top.ezadmin.blog.utils;



import top.ezadmin.common.utils.ArrayUtils;
import top.ezadmin.common.utils.DESUtils;
import top.ezadmin.common.utils.IpUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DoCookie {
    private static final Logger LOG =  LoggerFactory.getLogger(DoCookie.class);
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String domain="ezadmin.top";
    public DoCookie( ){
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        HttpServletResponse response = servletRequestAttributes.getResponse();
        this.request=request;
        this.response=response;
        if(!request.getServerName().contains("ezadmin.top") ){
            this.domain=request.getServerName();
        }
    }

    public String addEncodeCookie(String key, String value,Integer expire) throws Exception {
        String valueEncode=DESUtils.encryptDES(value);
               addCookie(key  , valueEncode,expire);
        return valueEncode;
    }
    public void addCookie(String key, String value,Integer expire){
        Cookie cookie=new Cookie(key, value);
         cookie.setDomain(domain);
        cookie.setPath("/");
        cookie.setMaxAge(expire);
        response.addCookie(cookie);
    }

    public  void deleteCookie(String key ){
        Cookie cookie = new Cookie(key, null);
        cookie.setDomain(domain);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
    public   String getCookie( String key ){

        String value="";
        Cookie[] cookies=request.getCookies();
        String org="cookie";
        if(!ArrayUtils.isEmpty(cookies)){
            for(Cookie cookie:cookies){
                if(StringUtils.equalsIgnoreCase(key,cookie.getName())){
                    value=cookie.getValue();
                }
            }
        }
        if(StringUtils.isBlank(value)||StringUtils.equalsIgnoreCase(value,"null")
                ||StringUtils.equalsIgnoreCase(value,"[object Object]")){
            return "";
        }
         return value;
    }
    public   String getDecodeCookie( String key ){

        String value="";
        Cookie[] cookies=request.getCookies();
        String org="cookie";
        if(!ArrayUtils.isEmpty(cookies)){
            for(Cookie cookie:cookies){
                if(StringUtils.equalsIgnoreCase(key,cookie.getName())){
                    value=cookie.getValue();
                }
            }
        }
        if(StringUtils.isBlank(value)||StringUtils.equalsIgnoreCase(value,"null")
                ||StringUtils.equalsIgnoreCase(value,"[object Object]")){
            return "";
        }
        try{
        return  DESUtils.decryptDES(value);
        }catch(Exception e){

            deleteCookie(key);
            LOG.warn("访问ID："+ IpUtils.getRealIp(request)+"\t访问URL："+request.getRequestURI()+"\t访问域名："+request.getServerName()+"\tcookie来源地："+org+"\t无法解析的cookie值："+value,e);
            return "";
        }
    }


}
