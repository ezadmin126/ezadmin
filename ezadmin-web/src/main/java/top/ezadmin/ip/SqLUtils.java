package top.ezadmin.ip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;
import java.util.stream.Stream;


public class SqLUtils {
    private final static Logger log = LoggerFactory.getLogger(SqLUtils.class);
    private static String badStrReg = "\\b(and|or)\\b.{1,6}?(=|>|<|\\bin\\b|\\blike\\b)|\\/\\*.+?\\*\\/|<\\s*script\\b|\\bEXEC\\b|UNION.+?SELECT|UPDATE.+?SET|INSERT\\s+INTO.+?VALUES|(SELECT|DELETE).+?FROM|(CREATE|ALTER|DROP|TRUNCATE)\\s+(TABLE|DATABASE)";

    private static Pattern sqlPattern = Pattern.compile(badStrReg, Pattern.CASE_INSENSITIVE);//整体都忽略大小写


    /**
     * get请求sql注入校验
     *
     * @param value
     * @return
     */
    public static boolean prevent(String value) throws UnsupportedEncodingException {


        String lowerValue = URLDecoder.decode(value, "UTF-8").toLowerCase();

        //获取到请求中所有参数值-取每个key=value组合第一个等号后面的值
        return Stream.of(lowerValue.split("\\&"))
                .map(kp -> kp.substring(kp.indexOf("=") + 1))
                .parallel()
                .anyMatch(param -> {
                    if (sqlPattern.matcher(param).find()) {
                        log.error("参数中包含不允许sql的关键词");
                        return true;
                    }
                    return false;
                });
    }

}
