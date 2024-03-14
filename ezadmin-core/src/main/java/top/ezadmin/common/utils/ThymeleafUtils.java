package top.ezadmin.common.utils;


import top.ezadmin.common.enums.ContentTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.io.Writer;


public class ThymeleafUtils {
    static Logger logger = LoggerFactory.getLogger(ThymeleafUtils.class);
    private static TemplateEngine stringEngine = new TemplateEngine();
    private static TemplateEngine classPathEngine = new TemplateEngine();

    public static void init(boolean cache ) {
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setCacheable(cache);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        stringEngine.setTemplateResolver(templateResolver);
        ClassLoaderTemplateResolver templateResolverClass = new ClassLoaderTemplateResolver();
        templateResolverClass.setPrefix("topezadmin/config/");
        templateResolverClass.setSuffix(".html");
        templateResolverClass.setTemplateMode(TemplateMode.HTML);
        templateResolverClass.setCacheable(cache);

        templateResolverClass.setCharacterEncoding("UTF-8");
        classPathEngine.setTemplateResolver(templateResolverClass);
    }
    public static void clear() {
        stringEngine.clearTemplateCache();
        classPathEngine.clearTemplateCache();
    }
    public static void process(String template, IContext context, Writer writer) {
        TemplateSpec spec = new TemplateSpec(template, null, ContentTypeEnum.HTML.value, null);
        classPathEngine.process(spec, context, writer);
    }
    public static String processString(String html, Context ctx) {
        if (StringUtils.isBlank(html)) {
            return html;
        }

        try {
            return stringEngine.process(html, ctx);
        } catch (Exception e) {
            logger.error("processString::{}",html, e);
            return "N/A";
        }finally {
           // Utils.addLog("结束渲染字符串Context");
        }
    }
}
