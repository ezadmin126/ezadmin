package top.ezadmin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.common.NotExistException;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.controller.*;
import top.ezadmin.plugins.templates.ThymeleafEzTemplate;
import top.ezadmin.web.EzResult;
import top.ezadmin.web.RequestContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EzBootstrap {
    private static Logger logger = LoggerFactory.getLogger(EzBootstrap.class);
    //确保单例
    private static EzBootstrap bootstrap = new EzBootstrap();
    private static EzBootstrapConfig ezBootstrapConfig = new EzBootstrapConfig();

    public static EzBootstrap getInstance() {
        return bootstrap;
    }

    private EzBootstrap() {
    }

    private static String includeShow = "(list|form)/([A-Za-z]+)-(.*)";
    private Pattern pInclude = null;


    public void init(EzBootstrapConfig config) {
        pInclude = Pattern.compile(config.getPrefixUrl()+includeShow);
        //初始化配置
        this.ezBootstrapConfig = config;
        //初始化模版
        ThymeleafEzTemplate.getIntance().init(config);

    }

    public static boolean clear() {
        config().getEzCache().clear();
        ThymeleafEzTemplate.getIntance().clearCache();
        return true;
    }


    public static EzBootstrapConfig config() {
        return ezBootstrapConfig;
    }


    public EzResult generate(RequestContext requestContext) {
        // 生成逻辑
        if (ezBootstrapConfig == null) {
            throw new RuntimeException("ezBootstrapConfig is not set");
        }
        String originatingUrl = requestContext.getRequestURI();
        originatingUrl = StringUtils.repaceAll(originatingUrl, "\\\\", "\\");
        int js = originatingUrl.indexOf(";");
        if (js >= 0) {
            originatingUrl = originatingUrl.substring(0, js);
        }
        if (originatingUrl.startsWith("/ezadmin/")) {
            originatingUrl = originatingUrl.replaceFirst("/ezadmin/", ezBootstrapConfig.getPrefixUrl());
        }
        if (originatingUrl.equals(ezBootstrapConfig.getPrefixUrl()+"index.html")) {
            IndexController controller2 = new IndexController();
            return controller2.index(requestContext);
        }
        //防止配置的前缀 有/
        originatingUrl = StringUtils.repaceAll(originatingUrl, "//", "/");
        Matcher m = pInclude.matcher(originatingUrl);
        if (m.find() && m.groupCount() == 3) {
            String contro = m.group(1);
            String method = m.group(2);
            String id = m.group(3);
            if (id.endsWith("\\")) {
                id = id.substring(0, id.length() - 2);
            }
            try {
                switch (contro) {
                    case "list":
                        return handleListController(requestContext, method, id);
                    case "form":
                        return handleFormController(requestContext, method, id);
                    default:
                        break;
                }
            } catch (NotExistException a) {
                return EzResult.instance().fail().code("404");
            } catch (Exception e) {
                logger.error("Controller execution error", e);
                return EzResult.instance().fail(e.getMessage());
            }
        }
        return EzResult.instance().fail("没有找到控制器").code("404");
    }



    public EzResult refresh(String... key) {
        // 刷新逻辑
        return EzResult.instance();
    }


    public EzResult destroy() {
        // 销毁逻辑
        return EzResult.instance();
    }

    private EzResult handleListController(RequestContext requestContext, String method, String id) throws Exception {
        ListController controller = new ListController();

        // 实现列表控制器逻辑
        switch (method) {
            case "page":
                return controller.page(requestContext, method, id);
            case "exportpage":
                return controller.exportpage(requestContext, method, id);
            case "data":
                return controller.data(requestContext, method, id);
            case "countpage":
                return controller.countpage(requestContext, method, id);
            case "pageCustomSearch":
                return controller.pageCustomSearch(requestContext, method, id);
        }
        return EzResult.instance().fail("没有找到方法").code("404");
    }

    private EzResult handleFormController(RequestContext requestContext, String method, String id) throws Exception {
        // 实现表单控制器逻辑
        FormController controller = new FormController();
        // 实现列表控制器逻辑
        EzResult result = EzResult.instance();
        switch (method) {
            case "page":
                return controller.page(requestContext, method, id);
            case "data":
                return controller.data(requestContext, method, id);
            case "submit":
                EzResult submit = controller.submit(requestContext, method, id);
                result.code("JSON").data(submit);
                return result;
            case "add":
                EzResult add = controller.add(requestContext, method, id);
                result.code("JSON").data(add);
                return result;
            case "delete":
                EzResult delete = controller.delete(requestContext, method, id);
                result.code("JSON").data(delete);
                return result;
            case "status":
                EzResult status = controller.status(requestContext, method, id);
                result.code("JSON").data(status);
                return result;
        }
        return EzResult.instance().fail("没有找到方法").code("404");
    }







    public javax.sql.DataSource getEzDataSource() {
        return ezBootstrapConfig.getDatasourceMap().get("defaultDataSource");
    }

    public javax.sql.DataSource getDataSourceByKey(Object key) {
        if (StringUtils.equalsIgnoreCase(key.toString(), "defaultDataSource")) {
            return getEzDataSource();
        }
        if (ezBootstrapConfig != null && ezBootstrapConfig.getDatasourceMap() != null) {
            return ezBootstrapConfig.getDatasourceMap().get(key.toString().toLowerCase());
        }
        return null;
    }
}
