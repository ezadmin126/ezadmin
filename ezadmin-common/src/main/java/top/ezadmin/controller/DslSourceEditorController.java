package top.ezadmin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.common.utils.*;
import top.ezadmin.web.EzResult;
import top.ezadmin.web.RequestContext;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DSL在线编辑控制器
 * 提供DSL配置文件的CRUD操作
 */
public class DslSourceEditorController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(DslSourceEditorController.class);

    // DSL配置文件的基础路径
    private static final String FORM_BASE_PATH = "topezadmin/config/layui/dsl/form/";
    private static final String LIST_BASE_PATH = "topezadmin/config/layui/dsl/list/";


    /**
     * 代码编辑器页面（使用ACE编辑器）
     * URL: /topezadmin/dsl/form/code-{formId} 或 /topezadmin/dsl/list-{listId}
     */
    public EzResult codeEditor(RequestContext requestContext, String method, String dslId) throws Exception {
        try {
            if (StringUtils.isBlank(dslId)) {
                return EzResult.instance().setSuccess(false).code("400").setMessage("DSL ID不能为空");
            }

            if (!"form".equals(method) && !"list".equals(method)) {
                return EzResult.instance().setSuccess(false).code("400").setMessage("DSL类型必须是form或list");
            }
            //获取dsl源码
            top.ezadmin.dao.dto.DslConfig dslConfig = DslLoader.loadDsl(dslId, method);
            String dslContent = "";
            if (dslConfig != null) {
                dslContent = JSONUtils.toPrettyJSONString(dslConfig.getConfig());
            }
            Map<String, Object> templateParam = new HashMap<>();
            templateParam.put("dslId", dslId);
            templateParam.put("dslType", method);
            templateParam.put("dslContent", dslContent);
            templateParam.put("title", "代码编辑器 - " + dslId);
            return render("layui/dsl/codeEditor", templateParam);
        } catch (Exception e) {
            logger.error("加载代码编辑器页面失败: {} / {}", method, dslId, e);
            return EzResult.instance().setSuccess(false).code("500").setMessage("加载代码编辑器失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有表单配置列表（API）
     * URL: /topezadmin/dsl/form/list
     */
    public EzResult formList(RequestContext requestContext, String method, String code) throws Exception {
        try {
            List<Map<String, Object>> formList = listConfigFiles(FORM_BASE_PATH, "form");
            return EzResult.instance().code("JSON").data(EzResult.instance().data(formList).count(formList.size()));
        } catch (Exception e) {
            logger.error("获取表单配置列表失败", e);
            return EzResult.instance().setSuccess(false).code("500").setMessage("获取表单配置列表失败: " + e.getMessage());
        }
    }


    /**
     * 保存表单配置
     * URL: /topezadmin/dsl/form/save-{formId}
     */
    public EzResult formSave(RequestContext requestContext, String method, String formId) throws Exception {
        try {
            if (StringUtils.isBlank(formId)) {
                return EzResult.instance().setSuccess(false).code("400").setMessage("表单ID不能为空");
            }

            String content = requestContext.getParameter("content");
            if (StringUtils.isBlank(content)) {
                return EzResult.instance().setSuccess(false).code("400").setMessage("配置内容不能为空");
            }

            // 验证JSON格式
            try {
                JSONUtils.deepParseObjectMap(content);
            } catch (Exception e) {
                return EzResult.instance().setSuccess(false).code("400").setMessage("配置内容JSON格式错误: " + e.getMessage());
            }

            String configPath = FORM_BASE_PATH + formId + ".json";
            boolean success = saveConfigFile(configPath, content);

            if (success) {
                return EzResult.instance().setMessage("保存成功");
            } else {
                return EzResult.instance().setSuccess(false).code("500").setMessage("保存失败");
            }
        } catch (Exception e) {
            logger.error("保存表单配置失败: {}", formId, e);
            return EzResult.instance().setSuccess(false).code("500").setMessage("保存表单配置失败: " + e.getMessage());
        }
    }

    /**
     * 保存表单配置
     * URL: /topezadmin/dsl/form/save-{formId}
     */
    public EzResult listSave(RequestContext requestContext, String method, String listId) throws Exception {
        try {
            if (StringUtils.isBlank(listId)) {
                return EzResult.instance().code("JSON").data(EzResult.instance().setMessage("列表ID不能为空"));
            }

            String content = requestContext.getParameter("content");
            if (StringUtils.isBlank(content)) {
                return EzResult.instance().code("JSON").data(EzResult.instance().setMessage("配置内容不能为空"));
            }
            // 验证JSON格式
            try {
                JSONUtils.deepParseObjectMap(content);
            } catch (Exception e) {
                return EzResult.instance().code("JSON").data(EzResult.instance().setMessage("配置内容JSON格式错误: " + e.getMessage()));
            }

            String configPath = LIST_BASE_PATH + listId + ".json";
            boolean success = saveConfigFile(configPath, content);
            if (success) {
                return EzResult.instance().code("JSON").data(EzResult.instance().setMessage("保存成功"));
            } else {
                return EzResult.instance().code("JSON").data(EzResult.instance().setMessage("保存失败"));
            }
        } catch (Exception e) {
            logger.error("保存列表配置失败: {}", listId, e);
            return EzResult.instance().code("JSON").data(EzResult.instance().setSuccess(false).code("500").setMessage("保存列表配置失败: " + e.getMessage()));
        }
    }


    // =============== 工具方法 ===============

    /**
     * 列出指定目录下的所有配置文件
     */
    private List<Map<String, Object>> listConfigFiles(String basePath, String type) throws Exception {
        List<Map<String, Object>> result = new ArrayList<>();

        String projectRoot = ProjectPathUtils.getProjectRoot();
        if (projectRoot == null) {
            logger.warn("无法获取项目根路径，无法列出配置文件");
            return result;
        }

        String dirPath = projectRoot + "/src/main/resources/" + basePath;
        File dir = new File(dirPath);

        if (!dir.exists() || !dir.isDirectory()) {
            logger.warn("配置目录不存在: {}", dirPath);
            return result;
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                try {
                    String fileName = file.getName();
                    String id = fileName.substring(0, fileName.length() - 5); // 去掉.json后缀

                    // 读取配置内容获取标题
                    String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                    Map<String, Object> config = JSONUtils.deepParseObjectMap(content);

                    Map<String, Object> item = new HashMap<>();
                    item.put("id", id);
                    item.put("title", Utils.trimNull(config.get("title")));
                    item.put("description", Utils.trimNull(config.get("description")));
                    item.put("type", type);
                    item.put("path", basePath + fileName);
                    item.put("lastModified", file.lastModified());

                    result.add(item);
                } catch (Exception e) {
                    logger.warn("读取配置文件失败: {}", file.getName(), e);
                }
            }
        }

        return result;
    }

    /**
     * 保存配置文件
     */
    private boolean saveConfigFile(String resourcePath, String content) throws Exception {
        String filePath = ProjectPathUtils.getResourceFilePath(resourcePath);
        if (filePath == null) {
            logger.error("无法获取项目文件路径: {}", resourcePath);
            return false;
        }

        try {
            Path path = Paths.get(filePath);

            // 确保目录存在
            File parentDir = path.toFile().getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            // 写入文件
            Files.write(path, content.getBytes(StandardCharsets.UTF_8));
            logger.info("保存配置文件成功: {}", filePath);
            return true;
        } catch (IOException e) {
            logger.error("保存配置文件失败: {}", filePath, e);
            return false;
        }
    }

}
