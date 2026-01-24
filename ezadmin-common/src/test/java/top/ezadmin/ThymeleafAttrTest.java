package top.ezadmin;

import org.junit.Test;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import top.ezadmin.plugins.templates.EzDialect;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ThymeleafAttrTest {

    @Test
    public void testEzAttrsDialect() {

        String template =
                "<div th:attrs=\"${propsAttrs}\">content</div>";

        Map<String, Object> props = new LinkedHashMap<>();
        props.put("data-id", "12345");
        props.put("title", "hello \"thymeleaf\"");
        props.put("disabled", true);
        props.put("hidden", false);

        Context ctx = new Context();
        ctx.setVariable("propsAttrs", props);

        TemplateEngine engine = new TemplateEngine();
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);
        engine.setTemplateResolver(resolver);

         engine.addDialect(new EzDialect());

        String result = engine.process(template, ctx);
        System.out.println(result);
    }

    @Test
    public void testEzAttrsDialect3() {

        String template =
                "<div th:text=\"${propsAttrs}+':'\">content</div>";

        Map<String, Object> props = new LinkedHashMap<>();
        props.put("data-id", "12345");
        props.put("title", "hello \"thymeleaf\"");
        props.put("disabled", true);
        props.put("hidden", false);

        Context ctx = new Context();
      //  ctx.setVariable("propsAttrs", props);

        TemplateEngine engine = new TemplateEngine();
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);
        engine.setTemplateResolver(resolver);

        engine.addDialect(new EzDialect());

        String result = engine.process(template, ctx);
        System.out.println(result);
    }

}
