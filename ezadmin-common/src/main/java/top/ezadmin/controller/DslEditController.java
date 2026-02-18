package top.ezadmin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.common.constants.SessionConstants;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.dao.dto.DslModificationRequest;
import top.ezadmin.service.DslAiService;
import top.ezadmin.web.EzResult;
import top.ezadmin.web.RequestContext;

import java.util.HashMap;
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
        return render("layui/dsl/edit",templateParam);
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

//    /**
//     * 创建新DSL
//     * URL: /topezadmin/inspector/list-create
//     */
//    public EzResult createDsl(RequestContext requestContext) {
//        try {
//            Map<String, Object> params = requestContext.getRequestParams();
//            log.info("收到创建DSL请求: {}", JSONUtils.toJSONString(params));
//
//            return handleCreateDsl(params);
//        } catch (Exception e) {
//            log.error("创建DSL异常", e);
//            return EzResult.instance().fail("创建失败: " + e.getMessage());
//        }
//    }

    /**
     * 处理创建DSL请求
     */
    private EzResult handleCreateDsl( DslModificationRequest request) {
//        String dslType = getStringParam(params, "dslType");
//        String userRequirement = getStringParam(params, "userRequirement");
//        String dataSource = getStringParam(params, "dataSource");

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
