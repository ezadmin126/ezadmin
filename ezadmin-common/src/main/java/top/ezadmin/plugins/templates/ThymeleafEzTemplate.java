package top.ezadmin.plugins.templates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import top.ezadmin.EzBootstrap;
import top.ezadmin.EzBootstrapConfig;
import top.ezadmin.common.enums.ContentTypeEnum;
import top.ezadmin.common.utils.ConfigFileLoader;
import top.ezadmin.common.utils.ProjectPathUtils;
import top.ezadmin.common.utils.Resources;
import top.ezadmin.common.utils.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;

public class ThymeleafEzTemplate implements EzTemplate {
    private static Logger logger = LoggerFactory.getLogger(ThymeleafEzTemplate.class);

    private static TemplateEngine stringEngine;

    private static TemplateEngine classPathEngine;

    private static TemplateEngine fileEngine;

    private static ThymeleafEzTemplate intance=new ThymeleafEzTemplate();
    private static TemplateUtils utils=new TemplateUtils();

    private static boolean initialized = false;

    public static ThymeleafEzTemplate getIntance(){
        return intance;
    }
    @Override
    public void init(EzBootstrapConfig config) {
        // 如果已经初始化，先重置引擎
        if (initialized) {
            logger.debug("Reinitializing template engines");
            stringEngine = null;
            classPathEngine = null;
            fileEngine=null;
        }

        // 创建新的 String template engine
        stringEngine = new TemplateEngine();
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setCacheable(config.isSqlCache());
        templateResolver.setTemplateMode(TemplateMode.HTML);
        stringEngine.setTemplateResolver(templateResolver);
        stringEngine.addDialect(new EzDialect());

        // 创建新的 ClassPath/File template engine
        classPathEngine = new TemplateEngine();
        classPathEngine.addDialect(new EzDialect());
        ITemplateResolver   fileTemplateResolver = createClassLoaderResolver(config.isSqlCache());
        classPathEngine.setTemplateResolver(fileTemplateResolver);


        initialized = true;
        if (!config.isSqlCache()) {
            // 开发模式：使用 FileTemplateResolver 实现热加载
            String projectRoot = ProjectPathUtils.getProjectRoot();
            if (projectRoot != null) {
                fileEngine = new TemplateEngine();
                fileEngine.addDialect(new EzDialect());
                FileTemplateResolver fileResolver = new FileTemplateResolver();
                fileResolver.setPrefix(projectRoot + "/src/main/resources/topezadmin/config/");
                fileResolver.setSuffix(".html");
                fileResolver.setTemplateMode(TemplateMode.HTML);
                fileResolver.setCacheable(false); // 关闭缓存实现热加载
                fileResolver.setCharacterEncoding("UTF-8");
                fileEngine.setTemplateResolver(fileResolver);
                logger.info("Template hot reload enabled, loading from: {}",
                    projectRoot + "/src/main/resources/topezadmin/config/");
            }
        }
    }

    /**
     * 创建基于 ClassLoader 的模板解析器
     */
    private ClassLoaderTemplateResolver createClassLoaderResolver(boolean cacheable) {
        ClassLoaderTemplateResolver templateResolverClass = new ClassLoaderTemplateResolver();
        templateResolverClass.setPrefix("topezadmin/config/");
        templateResolverClass.setSuffix(".html");
        templateResolverClass.setTemplateMode(TemplateMode.HTML);
        templateResolverClass.setCacheable(cacheable);
        templateResolverClass.setCharacterEncoding("UTF-8");
        return templateResolverClass;
    }

    @Override
    public String renderString(String content, Map<String,Object>   data) {
        // 确保引擎已初始化
        if (stringEngine == null) {
            logger.warn("Template engine not initialized, using default configuration");
            init(new EzBootstrapConfig());
        }

        org.thymeleaf.context.Context ctx = new Context();
        ctx.setVariables(data);
        ctx.setVariable("cr", utils);
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
        // 确保引擎已初始化
        if (classPathEngine == null) {
            logger.warn("Template engine not initialized, using default configuration");
            init(EzBootstrap.config());
        }

        org.thymeleaf.context.Context ctx = new Context();
        ctx.setVariables(data);
        ctx.setVariable("cr", utils);
        try {
            TemplateSpec spec = new TemplateSpec(path, null, ContentTypeEnum.HTML.value, null);
            if(!EzBootstrap.config().isSqlCache()){

                File file = new File(ProjectPathUtils.getProjectRoot()+  "/src/main/resources/topezadmin/config/"+path+".html");
                if(Files.exists(file.toPath())){
                    try {
                        return fileEngine.process(spec, ctx);
                    }catch (Exception e){
                        logger.info("从jar包中读取默认文件{}",path);
                        //从jar包里面读取
                        return classPathEngine.process(spec, ctx);
                    }
                }else{
                    return classPathEngine.process(spec, ctx);
                }
            }else{
                return classPathEngine.process(spec, ctx);
            }
        } catch (Exception e) {
            logger.error("processToString::{}", path, e);
           // throw e;
           return "<div style='display:none'>"+e.getMessage()+"</div>";
        }
    }
    @Override
    public boolean clearCache() {
        if (stringEngine != null) {
            stringEngine.clearTemplateCache();
        }
        if (classPathEngine != null) {
            classPathEngine.clearTemplateCache();
        }
        if (fileEngine != null) {
            fileEngine.clearTemplateCache();
        }
        return true;
    }
}
