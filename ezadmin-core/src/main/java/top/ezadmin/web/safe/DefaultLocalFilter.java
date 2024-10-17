package top.ezadmin.web.safe;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class DefaultLocalFilter {
    private static final Logger logger = LoggerFactory.getLogger(DefaultLocalFilter.class);

     private static Cache<String, String> BLACKLIST = null;


    public static void init(){
        if(BLACKLIST != null){
            return;
        }
        BLACKLIST=Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(50000)
                .build();

        SafeCallback urllimit=new SafeCallback() {
            @Override
            public void doCallback(String ip, long count, long period, long max) {
                logger.info("反爬告警 URL限流 {} {} {} {}",ip,count,period,max);
                BLACKLIST.put(ip,"URL限流");
            }
        };
        SafeCallback pool=new SafeCallback() {
            @Override
            public void doCallback(String ip, long count, long period, long max) {
                logger.info("反爬告警 IP池 {} {} {} {}",ip,count,period,max);
                BLACKLIST.put(ip,"IP池");
            }
        };
        SafeCallback night=new SafeCallback() {
            @Override
            public void doCallback(String ip, long count, long period, long max) {
                logger.info("反爬告警 异常时间段访问 {} {} {} {}",ip,count,period,max);
                BLACKLIST.put(ip,"异常时间段访问");
            }
        };
        SafeCallback per=new SafeCallback() {
            @Override
            public void doCallback(String ip, long count, long period, long max) {
                logger.info("反爬告警  区间访问 {} {} {} {}",ip,count,period,max);
                BLACKLIST.put(ip,"区间访问");
            }
        };
        RuleHolder.getInstance().init( urllimit,pool,night,per);
    }

    public static boolean isSafe(IpActionDto dto){
        if(BLACKLIST.getIfPresent(dto.getIp())!=null){
            logger.error("黑名单ip {} {}",dto.getIp(),BLACKLIST);
            return false;
        }
        if(sql(dto.getP())||xss(dto.getP())){
            logger.error("攻击代码 {}",dto.getP());
            BLACKLIST.put(dto.getIp(),"攻击代码");
            return false;
        }
        RuleHolder.getInstance().onMessage(dto);
        return true;
    }

    private static String badStrReg = "\\b(and|or)\\b.{1,6}?(=|>|<|\\bin\\b|\\blike\\b)|\\/\\*.+?\\*\\/|<\\s*script\\b|\\bEXEC\\b|UNION.+?SELECT|UPDATE.+?SET|INSERT\\s+INTO.+?VALUES|(SELECT|DELETE).+?FROM|(CREATE|ALTER|DROP|TRUNCATE)\\s+(TABLE|DATABASE)";

    private static Pattern sqlPattern = Pattern.compile(badStrReg, Pattern.CASE_INSENSITIVE);//整体都忽略大小写


    private static boolean sql(String value)   {
        try {
            String lowerValue = URLDecoder.decode(value, "UTF-8").toLowerCase();
            return Stream.of(lowerValue.split("\\&"))
                    .map(kp -> kp.substring(kp.indexOf("=") + 1))
                    .parallel()
                    .anyMatch(param -> {
                        if (sqlPattern.matcher(param).find()) {
                            return true;
                        }
                        return false;
                    });
        }catch (Exception e){
            logger.error(value,e);
            return true;}
    }
    private static final Pattern[] PATTERNS = {
            // Avoid anything in a <script> type of expression
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            // Avoid anything in a src='...' type of expression
            Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Remove any lonesome </script> tag
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            // Avoid anything in a <iframe> type of expression
            Pattern.compile("<iframe>(.*?)</iframe>", Pattern.CASE_INSENSITIVE),
            // Remove any lonesome <script ...> tag
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Remove any lonesome <img ...> tag
            Pattern.compile("<img(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Avoid eval(...) expressions
            Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Avoid expression(...) expressions
            Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            // Avoid javascript:... expressions
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            // Avoid vbscript:... expressions
            Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
            // Avoid onload= expressions
            Pattern.compile("on(load|error|mouseover|submit|reset|focus|click)(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };

    public static Boolean xss(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        for (Pattern scriptPattern : PATTERNS) {
            if (scriptPattern.matcher(value).find()) {
                return true;
            }
        }
        return false;
    }
}
