package top.ezadmin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.EzBootstrap;
import top.ezadmin.common.constants.SessionConstants;
import top.ezadmin.common.utils.DslLoader;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.dao.dto.DslModificationRequest;
import top.ezadmin.service.DslAiService;
import top.ezadmin.web.EzResult;
import top.ezadmin.web.RequestContext;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Inspector 控制器
 * 提供 DSL 修改相关的 API 接口
 * JDK8/JDK17兼容版本
 *
 *
 */
public class DslEditController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(DslEditController.class);
    private static final DslAiService dslAiService = DslAiService.getInstance();
    FormController formController=new FormController();
    /**
     *  URL: /topezadmin/edit/list-${id} ${id}为DSL id
     *       /topezadmin/edit/form-${id} ${id}为DSL id
     * @param requestContext
     * @param method
     * @param id
     * @return
     * @throws Exception
     */
    public EzResult page(RequestContext requestContext, String method, String id) throws Exception {
        Map<String, Object> templateParam=new HashMap<>();

        templateParam.put("EZ_SESSION_USER_NAME_KEY", Utils.trimNull(requestContext.getSessionParams().get(SessionConstants.EZ_SESSION_USER_NAME_KEY)));
        templateParam.put("EZ_SESSION_USER_ID_KEY", Utils.trimNull(requestContext.getSessionParams().get(SessionConstants.EZ_SESSION_USER_ID_KEY)));

        templateParam.put("id", id);
        templateParam.put("method", method);

        // 表单使用新的可视化编辑器
        if ("form".equals(method)) {
            String formId = id;
            // 使用统一加载器（文件优先，数据库降级）
            top.ezadmin.dao.dto.DslConfig dslConfig = DslLoader.loadDsl(formId, "form");
            if (dslConfig == null) {
                return EzResult.instance().code("404");
            }
            Map<String, Object> form = dslConfig.getConfig();
            if(StringUtils.isBlank((String)form.get("initUrl"))){
                form.put("initUrl", "/topezadmin/form/data-"+formId);
            }
            formController.iniFormItem(requestContext,form);
            templateParam.put("form", form);
            templateParam.put("requestContext",requestContext);
            templateParam.put("cacheFlag", EzBootstrap.config().isSqlCache());
            templateParam.put("ID",requestContext.getRequestParams().get("ID"));
            templateParam.put("uploadUrl",EzBootstrap.config().getUploadUrl());
            templateParam.put("downloadUrl",EzBootstrap.config().getDownloadUrl());
            templateParam.put("formSubmitUrl", "/topezadmin/form/submit-"+formId);
            templateParam.put("ENCRYPT_FORM_ID",  formId);
            templateParam.putAll(EzBootstrap.config().getConfig());
            return render("layui/dsl/form-edit", templateParam);
        }else{
            ListController listController = new ListController();
            // 使用统一加载器（文件优先，数据库降级）
            top.ezadmin.dao.dto.DslConfig dslConfig = DslLoader.loadDsl(id, "list");
            if (dslConfig == null) {
                return EzResult.instance().code("404");
            }
            Map<String, Object> list = dslConfig.getConfig();
            listController.initSearch(requestContext,list);
            Collection<String> tdtemplates=listController.initTd(list);
            if(list.get("initApi") == null){
                list.put("initApi", "/topezadmin/list/data-" + id);
            }
            //默认你不隐藏头部
            if(list.get("hideSearch") == null){
                list.put("hideSearch", false);
            }

            listController.initRowBtn(list);
            templateParam.put("list", list);
            templateParam.put("perPageInt", list.get("body") != null ? ((Map<String, Object>)list.get("body")).get("limit") : 10);
            templateParam.put("ENCRYPT_LIST_ID", id);
            templateParam.put("cacheFlag",EzBootstrap.config().isSqlCache());
            templateParam.put("tdTemplates", tdtemplates);
            templateParam.put("requestContext",requestContext);
            templateParam.put("downloadUrl",EzBootstrap.config().getDownloadUrl());
            templateParam.putAll(EzBootstrap.config().getConfig());

            templateParam.put("EZ_SESSION_USER_NAME_KEY", Utils.trimNull(requestContext.getSessionParams().get(SessionConstants.EZ_SESSION_USER_NAME_KEY)));
            templateParam.put("EZ_SESSION_USER_ID_KEY", Utils.trimNull(requestContext.getSessionParams().get(SessionConstants.EZ_SESSION_USER_ID_KEY)));
            // 列表继续使用原有的编辑器
            return render("layui/dsl/list-edit",templateParam);
        }
    }

    /**
     * 提交 DSL 修改请求
     * URL: /topezadmin/edit/submit-xxx
     *
     * @param requestContext 请求上下文
     * @param method 方法名
     * @param id URL编码
     * @return 修改结果
     */
    public EzResult submit(RequestContext requestContext, String method, String id) {
        try {
            // 从请求中获取参数
            Map<String, Object> params = requestContext.getRequestParams();
            log.info("收到 DSL 修改请求: {}", JSONUtils.toJSONString(params));
            // 构建请求对象
            DslModificationRequest request = new DslModificationRequest();
            request.setDslFileName(id);
            request.setDslType(getStringParam(params, "dslType"));
            request.setUserRequirement(getStringParam(params, "userRequirement"));
            request.setItem_name(getStringParam(params, "item_name"));
            request.setComponentCode(getStringParam(params, "component")); // 使用 component
            request.setDataSource(getStringParam(params, "dataSource"));
            request.setDslFileName(getStringParam(params, "dslFileName"));
            // 判断是否为新增模式
            Boolean isNew = (Boolean) params.get("isNew");
            if (isNew != null && isNew) {
                return handleCreateDsl(request);
            }


            //现有配置？
            request.setSelectedComponent(getStringParam(params, "component")); // 使用 component

            log.info("构建请求对象: dslFileName={}, dslType={}, component={}, item_name={}",
                    request.getDslFileName(), request.getDslType(),
                    request.getComponentCode(), request.getItem_name());

            // 参数校验
            if (request.getDslFileName() == null || request.getDslFileName().isEmpty()) {
                return EzResult.instance().code("JSON").fail().data(EzResult.instance().fail("DSL 文件名不能为空"));
            }

            if (request.getUserRequirement() == null || request.getUserRequirement().isEmpty()) {
                return EzResult.instance().code("JSON").fail().data(EzResult.instance().fail("用户需求描述不能为空"));
            }

            String dslType = request.getDslType();
            if (dslType == null || dslType.isEmpty()) {
                dslType = "form"; // 默认为表单类型
                request.setDslType(dslType);
            }
            if (!"form".equals(dslType) && !"list".equals(dslType)) {
                return EzResult.instance().code("JSON").fail().data(EzResult.instance().fail("DSL 类型必须是 form 或 list"));
            }
            // 调用服务处理
            return EzResult.instance().code("JSON"). data(dslAiService.modifyDsl(request));
        } catch (Exception e) {
            log.error("处理 DSL 修改请求异常", e);
            return EzResult.instance().code("JSON").fail().data(EzResult.instance().fail("系统异常: " + e.getMessage()));
        }
    }


    /**
     * 处理创建DSL请求
     */
    private EzResult handleCreateDsl( DslModificationRequest request) {

        // 参数校验
        if (request.getDslType() == null || request.getDslType().isEmpty()) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("summary", "缺少DSL类型参数");
            return EzResult.instance().fail().code("JSON"). data(EzResult.instance().fail().data(responseData));
        }

        if (!"form".equals(request.getDslType()) && !"list".equals(request.getDslType())) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("summary", "DSL类型必须是form或list");
            return EzResult.instance().fail().code("JSON"). data(EzResult.instance().fail().data(responseData));
        }

        if (request.getUserRequirement() == null || request.getUserRequirement().isEmpty()) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("summary", "请描述您的需求或提供SQL语句");
            return EzResult.instance().fail().code("JSON"). data(EzResult.instance().fail().data(responseData));
        }

        // 调用AI服务生成DSL
        EzResult result = dslAiService.createNewDsl(Utils.trimNullDefault(request.getDataSource(), "datasource"),request.getDslType(), request.getUserRequirement());
        return EzResult.instance().code("JSON"). data(result);
    }

    /**
     * 保存布局（表单或列表）
     * URL: /topezadmin/edit/save-layout-{id}
     *
     * @param requestContext 请求上下文
     * @param method 方法名
     * @param id URL编码
     * @return 保存结果
     */
    public EzResult saveLayout(RequestContext requestContext, String method, String id) {
        try {
            // 从请求中获取布局数据
            Map<String, Object> params = requestContext.getRequestParams();
            log.info("收到布局保存请求: dslId={}, data={}", id, JSONUtils.toJSONString(params));

            // 参数校验
            if (id == null || id.isEmpty()) {
                return EzResult.instance().code("JSON").fail().data(EzResult.instance().fail("DSL ID 不能为空"));
            }

            // 判断 DSL 类型（从布局数据判断，cardList=form, search=list）
            String dslType = "form";
            if (params.containsKey("search")) {
                dslType = "list";
            } else if (params.containsKey("cardList")) {
                dslType = "form";
            }

            log.info("检测到 DSL 类型: {}", dslType);

            // 调用服务保存布局
            EzResult result = dslAiService.saveLayout(id, dslType, params);

            if (result.isSuccess()) {
                log.info("布局保存成功: dslId={}, type={}", id, dslType);
                return EzResult.instance().code("JSON").success().data(result.getData());
            } else {
                log.warn("布局保存失败: dslId={}, type={}, reason={}", id, dslType, result.getMessage());
                return EzResult.instance().code("JSON").fail().data(result);
            }

        } catch (Exception e) {
            log.error("保存布局异常: dslId=" + id, e);
            return EzResult.instance().code("JSON").fail().data(EzResult.instance().fail("保存失败: " + e.getMessage()));
        }
    }

    /**
     * 创建 DSL 页面
     * URL: /topezadmin/edit/create-
     *
     * @param requestContext 请求上下文
     * @param method 方法名
     * @param id URL编码（这里不需要）
     * @return 创建页面
     */
    public EzResult create(RequestContext requestContext, String method, String id) throws Exception {
        Map<String, Object> templateParam = new HashMap<>();
        templateParam.put("EZ_SESSION_USER_NAME_KEY", Utils.trimNull(requestContext.getSessionParams().get(SessionConstants.EZ_SESSION_USER_NAME_KEY)));
        templateParam.put("EZ_SESSION_USER_ID_KEY", Utils.trimNull(requestContext.getSessionParams().get(SessionConstants.EZ_SESSION_USER_ID_KEY)));
        return render("layui/dsl/create-dsl", templateParam);
    }

    /**
     * 获取数据库表列表
     * URL: /topezadmin/edit/getTables-{datasource}
     *
     * @param requestContext 请求上下文
     * @param method 方法名
     * @param datasource 数据源名称
     * @return 表列表
     */
    public EzResult getTables(RequestContext requestContext, String method, String datasource) {
        try {
            // 获取数据源
            String dataSourceKey = Utils.trimNullDefault(datasource, "datasource");
            DataSource dataSource = EzBootstrap.config().getDatasourceMap().get(dataSourceKey);

            if (dataSource == null) {
                log.warn("数据源不存在: {}", dataSourceKey);
                return EzResult.instance().code("JSON").fail().data(EzResult.instance().fail("数据源不存在: " + dataSourceKey));
            }

            // 获取所有表信息（返回的是 JSON 字符串）
            String tablesJson = dslAiService.getTablesFromDataSource(dataSource);

            // 解析为 List<Map>
            List<Map<String, Object>> tableList = JSONUtils.parseListMapString(tablesJson);

            return EzResult.instance().code("JSON").success().data(EzResult.instance().data(tableList));

        } catch (Exception e) {
            log.error("获取数据库表列表失败", e);
            return EzResult.instance().code("JSON").fail().data(EzResult.instance().fail("获取表列表失败: " + e.getMessage()));
        }
    }

    /**
     * 辅助方法：从Map中获取字符串参数
     */
    private String getStringParam(Map<String, Object> params, String key) {
        Object value = params.get(key);
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}
