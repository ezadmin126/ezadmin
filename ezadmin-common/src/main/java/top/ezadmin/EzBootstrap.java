package top.ezadmin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.common.NotExistException;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.controller.*;
import top.ezadmin.dao.FormDao;
import top.ezadmin.dao.ListDao;
import top.ezadmin.dao.PluginsDao;
import top.ezadmin.plugins.templates.ThymeleafEzTemplate;
import top.ezadmin.web.EzResult;
import top.ezadmin.web.RequestContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EzBootstrap   {
    private static Logger logger = LoggerFactory.getLogger(EzBootstrap.class);
    //确保单例
    private static EzBootstrap bootstrap=new EzBootstrap();
    private static EzBootstrapConfig ezBootstrapConfig=new EzBootstrapConfig();

    public static EzBootstrap getInstance(){
        return bootstrap;
    }

    private EzBootstrap() {
    }
    private static String includeShow = "/topezadmin/(list|form|listEdit|formEdit|detail|edit|dsl)/([A-Za-z]+)-(.*)";
    private Pattern pInclude =null;

   
    public void init(EzBootstrapConfig config) {
        pInclude=Pattern.compile(includeShow);
        //初始化配置
        this.ezBootstrapConfig = config;
        //初始化模版
        ThymeleafEzTemplate.getIntance().init(config);
        //初始化插件
        PluginsDao.getInstance().init();
        //初始化静态列表
        ListDao.getInstance().init();
        //初始化静态表单
        FormDao.getInstance().init();
    }
    public static boolean clear(){
        config().getEzCache().clear();
        ListDao.getInstance().clear();
        FormDao.getInstance().clear();
        ThymeleafEzTemplate.getIntance().clearCache();
        return true;
    }

   
    public static EzBootstrapConfig config() {
        return ezBootstrapConfig;
    }


    public EzResult generate(RequestContext requestContext)   {
        // 生成逻辑
        if(ezBootstrapConfig == null){
            throw new RuntimeException("ezBootstrapConfig is not set");
        }
        String originatingUrl=requestContext.getRequestURI();
        originatingUrl = StringUtils.repaceAll(originatingUrl ,"\\\\", "\\");
        int js = originatingUrl.indexOf(";");
        if (js >= 0) {
            originatingUrl = originatingUrl.substring(0, js);
        }
        if(originatingUrl.startsWith("/ezadmin/")){
            originatingUrl=originatingUrl.replaceFirst("/ezadmin/","/topezadmin/");
        }else  if(StringUtils.isNotBlank(ezBootstrapConfig.getPrefixUrl())
                &&originatingUrl.startsWith(ezBootstrapConfig.getPrefixUrl())){
            originatingUrl=originatingUrl.replaceFirst(ezBootstrapConfig.getPrefixUrl(),"/topezadmin");
        }
        if(originatingUrl.equals("/topezadmin/index.html")){
            IndexController controller2=new IndexController();
            return controller2.index(requestContext );
        }
        //防止配置的前缀 有/
        originatingUrl = StringUtils.repaceAll(originatingUrl ,"//", "/");
        if(originatingUrl.equals("/topezadmin/listEdit/importlist.html")){
            originatingUrl="/topezadmin/listEdit/importlist-";
        }
        // DSL编辑器特殊URL处理
        if(originatingUrl.startsWith("/topezadmin/dsl/")){
            // /topezadmin/dsl/editor → /topezadmin/dsl/editor-
            if(originatingUrl.equals("/topezadmin/dsl/editor")){
                originatingUrl="/topezadmin/dsl/editor-";
            }
            // /topezadmin/dsl/form/xxx 转换为 /topezadmin/dsl/form-xxx
            // 但保持已有的"-"不变，例如 /topezadmin/dsl/form/get-abc 保持不变
            else {
                originatingUrl = originatingUrl.replaceFirst("/topezadmin/dsl/form/", "/topezadmin/dsl/form-");
            }
        }
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
                        return   handleFormController(requestContext, method, id);
                    case "listEdit":
                        return  handleListEditController(requestContext, method, id);
                    case "formEdit":
                        return  handleFormEditController(requestContext, method, id);
                     case "edit":
                         if(!EzBootstrap.config().isSqlCache()){
                             return handleEditController(requestContext, method, id);
                         }
                    case "inspector":
                        if(!EzBootstrap.config().isSqlCache()) {
                            return handleInspectorController(requestContext, method, id);
                        }
                    case "dsl":
                        return  handleDslController(requestContext, method, id);
                    default:
                        break;
                }
            }catch (NotExistException a){
                return  EzResult.instance().fail().code("404");
            }
            catch (Exception e) {
                logger.error("Controller execution error", e);
                return  EzResult.instance().fail(e.getMessage());
            }
        }
        return  EzResult.instance().fail("没有找到控制器");
    }

    private EzResult handleEditController(RequestContext requestContext, String method, String id) throws Exception {
        DslEditController controller=new DslEditController();
        if(method.equals("list")||method.equals("form")){
            return controller.page(requestContext,method,id);
        }else
        if(method.equals("submit")){
            return controller.submit(requestContext,method,id);
        }else
        if(method.equals("saveLayout")){
            return controller.saveLayout(requestContext,method,id);
        }else
        if(method.equals("create")){
            return controller.create(requestContext,method,id);
        }else
        if(method.equals("getTables")){
            return controller.getTables(requestContext,method,id);
        }
         return controller.page(requestContext,method,id);
    }


    public EzResult refresh(String... key) {
        // 刷新逻辑
        return  EzResult.instance();
    }

   
    public EzResult destroy() {
        // 销毁逻辑
        return  EzResult.instance();
    }

    private EzResult handleListController(RequestContext requestContext, String method, String id) throws Exception {
        if(method.equals("export")){
            ExportController controller=new ExportController();
            return controller.export(requestContext,method,id);
        }
        if(method.equals("edit")){
            DslEditController controller=new DslEditController();
            return controller.page(requestContext,method,id);
        }

        ListController controller=new ListController();

        // 实现列表控制器逻辑
        switch (method){
            case "page":
                return controller.page(  requestContext,method,id);
            case "exportpage":
                return controller.exportpage(  requestContext,method,id);
            case "data":
                return controller.data(  requestContext,method,id);
            case "countpage":
                return controller.countpage(  requestContext,method,id);
            case "list":
                return controller.list(  requestContext,method,id);
            case "count":
                return controller.count(  requestContext,method,id);
            case "tree":
                return controller.tree(  requestContext,method,id);
            case "treedata":
                return controller.treedata(  requestContext,method,id);
            case "trace":
                return controller.trace(  requestContext,method,id);
            case "api":
                return controller.api(  requestContext,method,id);
            case "selectCols":
                return controller.selectCols(  requestContext,method,id);
            case "customSearch":
                return controller.customSearch(  requestContext,method,id);
        }
        return EzResult.instance().fail("没有找到方法");
    }

    private EzResult handleFormController(RequestContext requestContext, String method, String id) throws Exception {
        // 实现表单控制器逻辑
        FormController controller=new FormController();
        // 实现列表控制器逻辑
        EzResult result=EzResult.instance();
        if(method.equals("edit")){
            DslEditController controller1=new DslEditController();
            return controller1.page(requestContext,method,id);
        }
        switch (method){
            case "page":
                return controller.page(  requestContext,method,id);
            case "data":
                return controller.data(  requestContext,method,id);
            case "form":
                return controller.form(  requestContext,method,id);
            case "trace":
                return controller.trace(  requestContext,method,id);
            case "doSubmit":
                EzResult  responsedoSubmit= controller.doSubmit(  requestContext,method,id);
                  result.code("JSON").data(responsedoSubmit);
                  return result;
            case "doDelete":
                EzResult  responsedoDelete= controller.doDelete(  requestContext,method,id);
                result.code("JSON").data(responsedoDelete);
                  return result;
            case "doStatus":
                EzResult  responsedoStatus= controller.doStatus(  requestContext,method,id);
                result.code("JSON").data(responsedoStatus);
                  return result;
            case "submit":
                EzResult  submit= controller.submit(  requestContext,method,id);
                result.code("JSON").data(submit);
                return result;
            case "delete":
                EzResult  delete= controller.delete(  requestContext,method,id);
                result.code("JSON").data(delete);
                return result;
            case "status":
                EzResult  status= controller.status(  requestContext,method,id);
                result.code("JSON").data(status);
                return result;
        }
        return EzResult.instance().fail("没有找到方法");
    }

    private EzResult handleListEditController(RequestContext requestContext, String method, String id) throws Exception {
        // 实现列表编辑控制器逻辑
        ListEditController controller=new ListEditController();
        switch (method){
            case "list":
                return controller.list(  requestContext,method,id);
            case "preview":
                return controller.preview(  requestContext,method,id);
            case "loadList":
                return controller.loadList(  requestContext,method,id);
            case "submitEdit":
                return  EzResult.instance().code("JSON").data(controller.submitEdit(  requestContext,method,id));
            case "publish":
                return  EzResult.instance().code("JSON").data( controller.publish(  requestContext,method,id));
            case "sourceEdit":
                return controller.sourceEdit(  requestContext,method,id);
            case "submitSourceEdit":
                return  EzResult.instance().code("JSON").data( controller.submitSourceEdit(  requestContext,method,id));
            case "trace":
                  controller.trace(  requestContext,method,id);
                  return EzResult.instance();
            case "importSql":
                return controller.importSql(  requestContext,method,id);
            case "importlist":
                return  EzResult.instance().code("JSON").data( controller.importlist(  requestContext,method,id));
        }
        return  EzResult.instance();
    }

    private EzResult handleFormEditController(RequestContext requestContext, String method, String id) throws Exception {
        // 实现表单编辑控制器逻辑
        FormEditController controller=new FormEditController();
        switch (method){
            case "trace":
                return controller.trace(  requestContext,method,id);
            case "submitEdit":
                return  EzResult.instance().code("JSON").data( controller.submitEdit(  requestContext,method,id));
            case "loadEdit":
                return controller.loadEdit(  requestContext,method,id);
            case "submitSourceEdit":
                return  EzResult.instance().code("JSON").data(  controller.submitSourceEdit(  requestContext,method,id));
            case "sourceEdit":
                return controller.sourceEdit(  requestContext,method,id);
            case "importSql":
                return controller.importSql(  requestContext,method,id);
            case "publish":
                return  EzResult.instance().code("JSON").data(  controller.publish(  requestContext,method,id));
            case "form":
                return controller.form(  requestContext,method,id);
            case "preview":
                return controller.preview(  requestContext,method,id);
            case "doSubmit":
                return  EzResult.instance().code("JSON").data( controller.doSubmit(  requestContext,method,id));
            case "doDelete":
                return  EzResult.instance().code("JSON").data( controller.doDelete(  requestContext,method,id));
            case "doStatus":
                return  EzResult.instance().code("JSON").data(  controller.doStatus(  requestContext,method,id));
        }
        return  EzResult.instance();
    }
 

    private EzResult handleInspectorController(RequestContext requestContext, String method, String id) throws Exception {
        // 实现API控制器逻辑
        // Inspector API: /topezadmin/api/submit-inspector
        if ("list".equals(method) || "form".equals(method)) {
            DslEditController controller = new DslEditController();
            return controller.page(requestContext, method, id);
        }
        if ("submit".equals(method)  ) {
            DslEditController controller = new DslEditController();
            return controller.submit(requestContext, method, id);
        }
        return  EzResult.instance();
    }
    //dsl/list-xx  dsl/form-xx  dsl/listsave-xx
    private EzResult handleDslController(RequestContext requestContext, String method, String id) throws Exception {
        // 实现DSL编辑器控制器逻辑
        DslSourceEditorController controller = new DslSourceEditorController();
        if ("list".equals(method) || "form".equals(method)) {
            return controller.codeEditor(requestContext, method, id);
        }
        if ("listsave".equals(method)  ) {
            return controller.listSave(requestContext, method, id);
        }
        if ("formsave".equals(method)) {
            return controller.formSave(requestContext, method, id);
        }
        return EzResult.instance().fail("没有找到方法");
    }




    public javax.sql.DataSource getEzDataSource() {
            return ezBootstrapConfig.getDatasourceMap().get("defaultDataSource");
    }

    public javax.sql.DataSource getDataSourceByKey(Object key) {
        if(StringUtils.equalsIgnoreCase(key.toString(),"defaultDataSource")){
            return getEzDataSource();
        }
        if (ezBootstrapConfig != null && ezBootstrapConfig.getDatasourceMap() != null) {
            return ezBootstrapConfig.getDatasourceMap().get(key.toString().toLowerCase());
        }
        return null;
    }
}
