package top.ezadmin.plugins.templates;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.util.StringUtils;
import org.unbescape.html.HtmlEscape;
import top.ezadmin.common.utils.JSONUtils;

import java.util.Map;

public class EzAttrsProcessor extends AbstractAttributeTagProcessor {

    private static final String ATTR_NAME = "attrs";
    private static final int PRECEDENCE = 1000;

    public EzAttrsProcessor(String dialectPrefix) {
        super(
                TemplateMode.HTML,
                dialectPrefix,
                null,
                false,
                ATTR_NAME,
                true,
                PRECEDENCE,
                true
        );
    }

    @Override
    protected void doProcess(
            ITemplateContext context,
            IProcessableElementTag tag,
            AttributeName attributeName,
            String attributeValue,
            IElementTagStructureHandler structureHandler) {

        // 1️⃣ 解析表达式 ${...}
        IStandardExpressionParser parser =
                StandardExpressions.getExpressionParser(context.getConfiguration());

        IStandardExpression expression = parser.parseExpression(context, attributeValue);
        Object value = expression.execute(context);

        if (!(value instanceof Map)) {
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> attrs = (Map<String, Object>) value;

        // 2️⃣ 注入属性
        for (Map.Entry<String, Object> entry : attrs.entrySet()) {
            String attrName = entry.getKey();
            Object attrValue = entry.getValue();

            if (attrValue == null) {
                continue;
            }
            //jquery validate对range有拦截，影响date字段
            if (StringUtils.equalsIgnoreCase(attrName, "range")) {
                continue;
            }

            // boolean 处理
            if (attrValue instanceof Boolean) {
                if ((Boolean) attrValue) {
                    structureHandler.setAttribute(attrName, attrName);
                }
                continue;
            }

            //嵌套map 处理
            if (attrValue instanceof Map) {
                structureHandler.setAttribute(attrName,  HtmlEscape.escapeHtml5(JSONUtils.toJSONString(attrValue)));
                continue;
            }

            // 普通值，自动 HTML 转义
            String escaped =
                    HtmlEscape.escapeHtml5(attrValue.toString());

            structureHandler.setAttribute(attrName, escaped);
        }

        // 3️⃣ 移除 ez:attrs 自身
        structureHandler.removeAttribute(attributeName);
    }
}
