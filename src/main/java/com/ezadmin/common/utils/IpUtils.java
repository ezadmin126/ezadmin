package com.ezadmin.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;


public class IpUtils {

    public final static Pattern IP_V4_PATTERN = Pattern.compile("^[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}$");


    /**
     * 从请求中获取真实的IP地址<br>
     * 优先级为：X-Forwarded-For > Proxy-Client-IP > WL-Proxy-Client-IP > HTTP_CLIENT_IP
     * > HTTP_X_FORWARDED_FOR > RemoteAddr
     *
     * @param request Http请求
     * @return true/false

     * @since 2012-9-7
     */

    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (isIpUnknown(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (isIpUnknown(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isIpUnknown(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isIpUnknown(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isIpUnknown(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isIpUnknown(ip)) {
            ip = request.getRemoteAddr();
        } else {
            return ip;
        }
        return ip;
    }

    public static boolean isIpUnknown(String ip) {
        return StringUtils.isBlank(ip) || !IP_V4_PATTERN.matcher(ip).matches() || "unknown".equalsIgnoreCase(ip);
    }

}
