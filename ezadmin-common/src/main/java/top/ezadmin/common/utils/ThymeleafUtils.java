package top.ezadmin.common.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


public class ThymeleafUtils {
    static Logger logger = LoggerFactory.getLogger(ThymeleafUtils.class);
    private static TemplateEngine stringEngine = new TemplateEngine();
    private static TemplateEngine classPathEngine = new TemplateEngine();



    public static String processString(String html, Context ctx) {
        if (StringUtils.isBlank(html)) {
            return html;
        }

        try {
            return stringEngine.process(html, ctx);
        } catch (Exception e) {
            logger.error("processString::{}", html, e);
            return "N/A";
        } finally {
            // Utils.addLog("结束渲染字符串Context");
        }
    }
    

}
