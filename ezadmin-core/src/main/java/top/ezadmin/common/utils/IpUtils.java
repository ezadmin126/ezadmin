package top.ezadmin.common.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;


public class IpUtils {
    private static final String IPV4_REGEX = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    private static final String IPV6_REGEX = "^([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4})$";
    private static final Pattern IPV4PATTERN = Pattern.compile(IPV4_REGEX);
    private static final Pattern IPV6PATTERN = Pattern.compile(IPV6_REGEX);
    private static final String X_FORWARDED_FOR = "x-forwarded-for";
    private static final String X_Real_IP = "X-Real-IP";
    private static final String PROXY_CLIENT_IP = "Proxy-Client-IP";
    private static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";

    public static boolean isIpUnknown(String ip) {
        return StringUtils.isBlank(ip) || (!IPV4PATTERN.matcher(ip).matches() && !IPV6PATTERN.matcher(ip).matches());
    }

    public static String getRealIp(HttpServletRequest request) {
        String ip = cut(request.getHeader(X_Real_IP));
        if (isIpUnknown(ip)) {
          ip = cut(request.getHeader(X_FORWARDED_FOR));
        }
        if (isIpUnknown(ip)) {
            ip = cut(request.getHeader(PROXY_CLIENT_IP));
        }
        if (isIpUnknown(ip)) {
            ip = cut(request.getHeader(WL_PROXY_CLIENT_IP));
        }
        if (isIpUnknown(ip)) {
            ip = cut(request.getRemoteAddr());
        }
        return Utils.trimNull(ip);
    }

    private static String cut(String ip) {
        if (StringUtils.isBlank(ip)) {
            return ip;
        }
        String str = ",";
        if (ip.indexOf(str) > 0) {
            ip = ip.substring(0, ip.indexOf(str));
        }
        return ip;
    }
}
