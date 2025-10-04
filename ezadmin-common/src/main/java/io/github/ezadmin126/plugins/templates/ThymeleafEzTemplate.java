package io.github.ezadmin126.plugins.templates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import io.github.ezadmin126.EzBootstrapConfig;
import io.github.ezadmin126.common.enums.ContentTypeEnum;
import io.github.ezadmin126.common.utils.StringUtils;

import java.util.Map;

public class ThymeleafEzTemplate implements EzTemplate {
    private static Logger logger = LoggerFactory.getLogger(ThymeleafEzTemplate.class);

    private static TemplateEngine stringEngine = new TemplateEngine();

    private static TemplateEngine classPathEngine = new TemplateEngine();

    private static ThymeleafEzTemplate intance=new ThymeleafEzTemplate();
    public static ThymeleafEzTemplate getIntance(){
        return intance;
    }
    @Override
    public void init(EzBootstrapConfig config) {
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setCacheable(config.isSqlCache());
        templateResolver.setTemplateMode(TemplateMode.HTML);
        stringEngine.setTemplateResolver(templateResolver);
        ClassLoaderTemplateResolver templateResolverClass = new ClassLoaderTemplateResolver();
        templateResolverClass.setPrefix("topezadmin/config/");
        templateResolverClass.setSuffix(".html");
        templateResolverClass.setTemplateMode(TemplateMode.HTML);
        templateResolverClass.setCacheable(config.isSqlCache());
        templateResolverClass.setCharacterEncoding("UTF-8");
        classPathEngine.setTemplateResolver(templateResolverClass);
    }

    @Override
    public String renderString(String content, Map<String,Object>   data) {
        org.thymeleaf.context.Context ctx = new Context();
        ctx.setVariables(data);
        if (StringUtils.isBlank(content)) {
            return content;
        }

        try {
            return stringEngine.process(content,   ctx);
        } catch (Exception e) {
            logger.error("processString::{}", content, e);
            return "N/A";
        } finally {
            // Utils.addLog("结束渲染字符串Context");
        }
    }

    @Override
    public String renderFile(String path, Map<String,Object> data) {
        org.thymeleaf.context.Context ctx = new Context();
        ctx.setVariables(data);
        try {
            TemplateSpec spec = new TemplateSpec(path, null, ContentTypeEnum.HTML.value, null);
            return classPathEngine.process(spec, ctx);
        } catch (Exception e) {
            logger.error("processToString::{}", path, e);
            return "N/A";
        }
    }

    @Override
    public boolean clearCache() {
        stringEngine.clearTemplateCache();
        classPathEngine.clearTemplateCache();
        return true;
    }
}
