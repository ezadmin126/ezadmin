package top.ezadmin.plugins.templates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.EzBootstrap;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.Utils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 模板工具类，用于在Thymeleaf模板中调用各种工具方法
 */
public class TemplateUtils {
    private static Logger logger = LoggerFactory.getLogger(TemplateUtils.class);
    public  String render(Map<String, Object>  param) {
        if(logger.isDebugEnabled()){
            logger.debug("开始渲染插件{}",JSONUtils.toJSONString(param));
        }
        String type= Utils.trimEmptyDefault(param.get("component"),"input");

        param.put("propsJson", JSONUtils.toJSONString(param.get("props")));
        if(!EzBootstrap.config().isSqlCache()){
            param.put("componentJson", JSONUtils.toJSONString(param));
        }
        String result=  EzBootstrap.config().getEzTemplate().renderFile("layui/dsl/component/"+type, param);
        if(logger.isDebugEnabled()){
            logger.debug("渲染结果{}",result);
        }
        return result;
    }
}