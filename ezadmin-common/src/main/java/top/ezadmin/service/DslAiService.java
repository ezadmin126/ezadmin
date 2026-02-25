package top.ezadmin.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.EzBootstrap;
import top.ezadmin.common.utils.ConfigFileLoader;
import top.ezadmin.common.utils.DslLoader;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.ProjectPathUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.dao.Dao;
import top.ezadmin.dao.dto.DslModificationRequest;
import top.ezadmin.web.EzResult;

import javax.sql.DataSource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DSL AI 修改服务
 * 负责通过 AI 分析用户需求并修改 DSL 配置
 * JDK8/JDK17兼容版本
 */
public class DslAiService {
    private static final Logger log = LoggerFactory.getLogger(DslAiService.class);
    private static final ChatGptService chatGptService = ChatGptService.getInstance();
    private static final DslAiService instance = new DslAiService();

    private String dslBasePath = "topezadmin/config/layui/dsl";

    private DslAiService() {
    }

    public static DslAiService getInstance() {
        return instance;
    }

    /**
     * 创建新的DSL配置
     * @param dslType DSL类型：form或list
     * @param userRequirement 用户需求描述或SQL语句
     * @return 创建结果
     */
    public EzResult createNewDsl(String dataSource,String dslType, String userRequirement) {
        try {
            log.info("开始创建新DSL: type={}, requirement={}", dslType, userRequirement);
            StringBuilder resultInfoMessage=new StringBuilder();
            //1.先根据用户的需求，判断与哪些库表有关系
            DataSource dataSource1= EzBootstrap.config().getDatasourceMap().get(dataSource);
            String tables = getTablesFromDataSource(dataSource1);
            String relatedTables = getRelatedTables(userRequirement, tables);
            resultInfoMessage.append("获取到的相关表为：").append(relatedTables).append("\n");
            //获取每个表的详细信息
            String tableDetails=getTablesDetailFromDataSource(dataSource1, relatedTables);



            // 1. 加载创建DSL的提示词模板
            String promptTemplate = loadCreateDslPrompt(dslType);
            if (promptTemplate == null) {
                Map<String, Object> responseData = new HashMap<String, Object>();
                resultInfoMessage.append("创建DSL提示词模板加载失败").append("\n");
                responseData.put("summary", resultInfoMessage);
                return EzResult.instance().fail().data(responseData);
            }

            // 2. 加载组件文档索引（新增模式不需要选中组件信息）
            String componentDocs = loadComponentDocsIndex(null);

            // 3. 构建系统提示词
            String systemPrompt = promptTemplate + "\n\n" + componentDocs;
            //获取所有的数据库表名

            systemPrompt += "\n\n 当前数据库中关联的表信息为为：" +tableDetails;

            // 4. 构建用户提示词
            String userPrompt = buildCreateDslUserPrompt(dslType, userRequirement);

            // 5. 调用AI生成DSL
            Map<String, Object> aiResponse = chatGptService.chatForJson(systemPrompt, userPrompt);

            // 6. 提取生成的DSL和summary
            Object dslContentObj = aiResponse.get("dsl");
            String summary = (String) aiResponse.get("summary");
            resultInfoMessage.append(summary).append("DSL创建成功").append("\n");
            String suggestedId = (String) aiResponse.get("id");

            if (dslContentObj == null) {
                Map<String, Object> responseData = new HashMap<String, Object>();
                resultInfoMessage.append("AI未返回有效的DSL配置").append("\n");
                responseData.put("summary", resultInfoMessage);
                return EzResult.instance().fail().data(responseData);
            }

            // 7. 生成DSL文件名（如果AI没有建议，则使用时间戳）
            String dslId = suggestedId;
            if (dslId == null || dslId.isEmpty()) {
                dslId = dslType + "-" + System.currentTimeMillis();
            }

            // 8. 保存DSL文件
            Map<String, Object> dslContent = (Map<String, Object>) dslContentObj;
            boolean saved = saveDslFile(dslId, dslType, dslContent);

            if (!saved) {
                Map<String, Object> responseData = new HashMap<String, Object>();

                resultInfoMessage.append("DSL文件保存失败").append("\n");
                responseData.put("summary", resultInfoMessage);
                return EzResult.instance().fail().data(responseData);
            }

            // 9. 返回成功结果
            Map<String, Object> responseData = new HashMap<String, Object>();

            responseData.put("dslId", dslId);
            responseData.put("dslType", dslType);
            responseData.put("filePath", getDslFilePath(dslId, dslType));
            if(dslType.equals("form")){
                responseData.put("redirectUrl", "/topezadmin/edit/form-"+dslId);
            }else{
                responseData.put("redirectUrl", "/topezadmin/edit/list-"+dslId);
            }
            resultInfoMessage.append("DSL文件保存失败").append("\n");
            responseData.put("summary", resultInfoMessage);

            log.info("DSL创建成功: id={}, type={}, summary={}", dslId, dslType,resultInfoMessage);
            return EzResult.instance().data(responseData);

        } catch (Exception e) {
            log.error("创建DSL失败", e);
            Map<String, Object> responseData = new HashMap<String, Object>();
            responseData.put("summary", "创建失败: " + e.getMessage());
            return EzResult.instance().fail().data(responseData);
        }
    }

    private String getRelatedTables(String userRequirement,String tables) {
        Map<String, Object> aiResponse = chatGptService.chatForJson("##返回要求：\n" +
                        "1. 只返回一个JSON对象\n" +
                        "2. 格式如下：\n" +
                        "\n```json" +
                        "{\n" +
                        "  \"related_tables\":  \"逗号分隔的表名，如： table1,table2\",\n" +
                        "  \"summary\": \"返回表集合概要，给用户看的\"\n" +
                        "}\n" +
                        "```\n" +
                        "3. 不要添加解释\n"
                      ,
                "请根据用户需求，判断与哪些库表有关系，并返回结果。用户需求：" + userRequirement + "\n\n，当前表清单：" + tables);
        if(aiResponse.containsKey("related_tables")){
                return Utils.trimNull(aiResponse.get("related_tables"));
        }
        return "";
    }

    public String getTablesFromDataSource(DataSource dataSource1) {
        try {
            List<Map<String, Object>> tables = Dao.getInstance().executeQuery(dataSource1, "SELECT  TABLE_NAME,TABLE_COMMENT FROM information_schema.tables where TABLE_TYPE='BASE TABLE' AND TABLE_SCHEMA=DATABASE()\n",
                    null);
            return JSONUtils.toJSONString(tables);
        } catch (Exception e) {
            log.warn("获取表信息失败",e);
        }
        return "";
    }
    private String getTablesDetailFromDataSource(DataSource dataSource1,String tableNames) {
        StringBuilder stringBuilder = new StringBuilder();;
        try {
            String sql="SELECT\n" +
                    "    COLUMN_NAME,\n" +
                    "    DATA_TYPE,\n" +
                    "    CHARACTER_MAXIMUM_LENGTH AS SIZE,\n" +
                    "    IS_NULLABLE,\n" +
                    "    COLUMN_DEFAULT,\n" +
                    "    COLUMN_COMMENT,\n" +
                    "    EXTRA  \n" +
                    "FROM\n" +
                    "    INFORMATION_SCHEMA.COLUMNS\n" +
                    "WHERE\n" +
                    "    TABLE_SCHEMA = DATABASE()\n" +
                    "    AND TABLE_NAME = ?\n" +
                    "ORDER BY\n" +
                    "    ORDINAL_POSITION";
            String tables[] = tableNames.split(",");

            for (String table : tables) {
                List<Map<String, Object>> tableColumns = Dao.getInstance().executeQuery(dataSource1, sql,
                        new Object[]{table.trim()});
                stringBuilder.append(JSONUtils.toJSONString(tableColumns));
            }

        } catch (Exception e) {
            log.warn("获取表结构信息失败",e);
        }
        return stringBuilder.toString();
    }

    /**
     * 处理 DSL 修改请求（使用 JSON Patch RFC6902）
     */
    public EzResult modifyDsl(DslModificationRequest  request) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            log.info("开始处理 DSL 修改请求: {}", JSONUtils.toJSONString(request));
            StringBuilder resultInfoMessage = new StringBuilder();

            // 1. 读取 DSL 文件
            Map<String, Object> dslContent = loadDslFile(request.getDslFileName(), request.getDslType());
            if (dslContent == null) {
                Map<String, Object> responseData = new HashMap<String, Object>();
                responseData.put("summary", "DSL 文件不存在: " + request.getDslFileName());
                return EzResult.instance().fail().data(responseData);
            }


            //1.先根据用户的需求，判断与哪些库表有关系
            DataSource dataSource1= EzBootstrap.config().getDatasourceMap().get(Utils.trimNullDefault(dslContent.get("datasource"),"datasource" ));
            String tables = getTablesFromDataSource(dataSource1);
            String relatedTables = getRelatedTables(request.getUserRequirement(), tables);
            resultInfoMessage.append("获取到的相关表为：").append(relatedTables).append("\n");
            //获取每个表的详细信息
            String tableDetails=getTablesDetailFromDataSource(dataSource1, relatedTables);

            // 2. 加载提示词模板
            String promptTemplate = loadPromptTemplate(request.getDslType());
            if (promptTemplate == null) {
                Map<String, Object> responseData = new HashMap<String, Object>();
                responseData.put("summary", "提示词模板加载失败");
                return EzResult.instance().fail().data(responseData);
            }

            // 3. 加载组件文档索引
            String componentDocs = loadComponentDocsIndex(request);

            // 4. 构建系统提示词：模板 + 组件文档
            String systemPrompt = promptTemplate + "\n\n" + componentDocs+ "\n\n相关库表为：" + tableDetails;


            // 5. 构建用户提示词：DSL 内容 + 用户需求
            String userPrompt = buildUserPrompt(dslContent, request);

            // 6. 调用 AI 获取修改结果（patch 格式）
            Map<String, Object> aiResponse = chatGptService.chatForJson(systemPrompt, userPrompt);

            // 7. 提取 patch 和 summary
            Object patchObj = aiResponse.get("patch");
            String summary = (String) aiResponse.get("summary");

            if (patchObj == null) {
                Map<String, Object> responseData = new HashMap<String, Object>();
                responseData.put("summary", resultInfoMessage.toString() + "\n" + summary);
                return EzResult.instance().fail().data(responseData);
            }
            try {
                // 8. 应用 JSON Patch
                String originalJson = JSONUtils.toJSONString(dslContent);
                JsonNode original = mapper.readTree(originalJson);

                String patchJson = JSONUtils.toJSONString(patchObj);
                JsonNode patchNode = mapper.readTree(patchJson);

                JsonPatch patch = JsonPatch.fromJson(patchNode);
                JsonNode result = patch.apply(original);
                // 9. 转换回 Map 并保存
                Map<String, Object> modifiedDsl = mapper.readValue(result.toString(), Map.class);
                boolean saved = saveDslFile(request.getDslFileName(), request.getDslType(), modifiedDsl);
                if (!saved) {
                    return EzResult.instance().fail("DSL 文件保存失败");
                }
            }catch (Exception e){
                Map<String, Object> responseData = new HashMap<String, Object>();
                responseData.put("summary", resultInfoMessage.toString() + "\nDSL 文件保存失败: " + summary);
                return EzResult.instance().fail().data(responseData);
            }
            // 10. 构建响应
            resultInfoMessage.append(summary);
            Map<String, Object> responseData = new HashMap<String, Object>();
            responseData.put("summary", resultInfoMessage.toString());
            responseData.put("patch", patchObj);
          //  responseData.put("file_path", getDslFilePath(request.getDslFileName(), request.getDslType()));
            log.info("DSL 修改完成，文件已保存: {}, 摘要: {}", responseData.get("file_path"), summary);
            return EzResult.instance().data(responseData);
        } catch (Exception e) {
            log.error("处理 DSL 修改请求失败", e);
            return EzResult.instance().fail("处理失败: " + e.getMessage());
        }
    }

    /**
     * 保存 DSL 到文件或数据库
     * 支持双写策略
     */
    private boolean saveDslFile(String fileName, String dslType, Map<String, Object> dslContent) {
        try {
            // 默认策略：同时保存到文件和数据库
            boolean saveToFile = true;
            boolean saveToDatabase = true;

            // 调用统一保存方法
            boolean success = DslLoader.saveDsl(fileName, dslType, dslContent, saveToFile, saveToDatabase);

            if (success) {
                log.info("DSL 保存成功: fileName={}, type={}", fileName, dslType);
            } else {
                log.error("DSL 保存失败: fileName={}, type={}", fileName, dslType);
            }

            return success;

        } catch (Exception e) {
            log.error("保存 DSL 失败", e);
            return false;
        }
    }

    /**
     * 保存布局（表单或列表）
     * 根据前端传来的布局数据重新排序 DSL
     *
     * @param dslId DSL ID
     * @param dslType DSL 类型：form 或 list
     * @param layoutData 布局数据
     * @return 保存结果
     */
    public EzResult saveLayout(String dslId, String dslType, Map<String, Object> layoutData) {
        try {
            log.info("开始保存布局: dslId={}, type={}", dslId, dslType);

            // 1. 加载现有的 DSL 文件
            Map<String, Object> dslContent = loadDslFile(dslId, dslType);
            if (dslContent == null) {
                return EzResult.instance().fail("DSL 文件不存在: " + dslId);
            }

            // 2. 根据类型处理不同的布局数据
            if ("form".equals(dslType)) {
                // 处理表单布局
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> cardsLayout = (List<Map<String, Object>>) layoutData.get("cardList");

                if (cardsLayout == null || cardsLayout.isEmpty()) {
                    return EzResult.instance().fail("表单布局数据为空");
                }

                // 更新 DSL 中的 cardList
                dslContent.put("cardList", cardsLayout);

            } else if ("list".equals(dslType)) {
                // 处理列表布局
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> searchLayout = (List<Map<String, Object>>) layoutData.get("search");

                if (searchLayout == null || searchLayout.isEmpty()) {
                    return EzResult.instance().fail("列表搜索布局数据为空");
                }

                // 更新 DSL 中的 search
                dslContent.put("search", searchLayout);

            } else {
                return EzResult.instance().fail("不支持的 DSL 类型: " + dslType);
            }

            // 3. 保存 DSL 文件
            boolean success = saveDslFile(dslId, dslType, dslContent);

            if (success) {
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("summary", dslType.equals("form") ? "表单布局保存成功" : "列表搜索布局保存成功");
                responseData.put("dslId", dslId);
                responseData.put("dslType", dslType);
                return EzResult.instance().success().data(responseData);
            } else {
                return EzResult.instance().fail("保存 DSL 文件失败");
            }

        } catch (Exception e) {
            log.error("保存布局失败: dslId=" + dslId + ", type=" + dslType, e);
            return EzResult.instance().fail("保存失败: " + e.getMessage());
        }
    }

    /**
     * 重新排序 cards
     */
    private List<Map<String, Object>> reorderCards(
            List<Map<String, Object>> originalCards,
            List<Map<String, Object>> cardsLayout) {

        List<Map<String, Object>> newCardList = new java.util.ArrayList<>();

        for (Map<String, Object> cardLayout : cardsLayout) {
            String targetItemName = (String) cardLayout.get("item_name");

            // 在原始 cardList 中查找对应的 card
            Map<String, Object> originalCard = findCardByItemName(originalCards, targetItemName);

            if (originalCard != null) {
                // 重新排序 card 内的 rows 和 fields
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> rowsLayout = (List<Map<String, Object>>) cardLayout.get("rows");

                if (rowsLayout != null && !rowsLayout.isEmpty()) {
                    Map<String, Object> reorderedCard = reorderCardFields(originalCard, rowsLayout);
                    newCardList.add(reorderedCard);
                } else {
                    newCardList.add(originalCard);
                }
            } else {
                log.warn("未找到 item_name={} 的 card", targetItemName);
            }
        }

        return newCardList;
    }

    /**
     * 根据 item_name 查找 card
     */
    private Map<String, Object> findCardByItemName(List<Map<String, Object>> cards, String itemName) {
        for (Map<String, Object> card : cards) {
            String cardLabel = (String) card.get("label");
            if (cardLabel != null && ("card-" + cardLabel).equals(itemName)) {
                return card;
            }
        }
        return null;
    }

    /**
     * 重新排序 card 内的 fields
     */
    private Map<String, Object> reorderCardFields(
            Map<String, Object> originalCard,
            List<Map<String, Object>> rowsLayout) {

        // 创建 card 的副本
        Map<String, Object> newCard = new HashMap<>(originalCard);

        @SuppressWarnings("unchecked")
        List<List<Map<String, Object>>> originalFieldList =
                (List<List<Map<String, Object>>>) originalCard.get("fieldList");

        if (originalFieldList == null || originalFieldList.isEmpty()) {
            return newCard;
        }

        // 创建新的 fieldList
        List<List<Map<String, Object>>> newFieldList = new java.util.ArrayList<>();

        for (Map<String, Object> rowLayout : rowsLayout) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> fieldsLayout = (List<Map<String, Object>>) rowLayout.get("fields");

            if (fieldsLayout == null || fieldsLayout.isEmpty()) {
                continue;
            }

            List<Map<String, Object>> newRow = new java.util.ArrayList<>();

            for (Map<String, Object> fieldLayout : fieldsLayout) {
                String targetItemName = (String) fieldLayout.get("item_name");
                Integer targetWidth = (Integer) fieldLayout.get("width");

                // 在原始 fieldList 中查找对应的 field
                Map<String, Object> originalField = findFieldByItemName(originalFieldList, targetItemName);

                if (originalField != null) {
                    // 创建 field 的副本并更新宽度
                    Map<String, Object> newField = new HashMap<>(originalField);

                    // 更新 classAppend 中的宽度
                    if (targetWidth != null) {
                        String classAppend = (String) newField.get("classAppend");
                        if (classAppend != null) {
                            // 移除旧的 layui-col-md*
                            classAppend = classAppend.replaceAll("layui-col-md\\d+", "");
                        } else {
                            classAppend = "";
                        }
                        // 添加新的宽度
                        classAppend = ("layui-col-md" + targetWidth + " " + classAppend).trim();
                        newField.put("classAppend", classAppend);
                    }

                    newRow.add(newField);
                } else {
                    log.warn("未找到 item_name={} 的 field", targetItemName);
                }
            }

            if (!newRow.isEmpty()) {
                newFieldList.add(newRow);
            }
        }

        newCard.put("fieldList", newFieldList);
        return newCard;
    }

    /**
     * 在所有 rows 中查找指定 item_name 的 field
     */
    private Map<String, Object> findFieldByItemName(
            List<List<Map<String, Object>>> fieldList,
            String itemName) {

        for (List<Map<String, Object>> row : fieldList) {
            for (Map<String, Object> field : row) {
                String fieldItemName = (String) field.get("item_name");
                if (itemName.equals(fieldItemName)) {
                    return field;
                }
            }
        }
        return null;
    }

    /**
     * 获取 DSL 文件的绝对路径
     */
    private String getDslFilePath(String fileName, String dslType) {
        // 获取项目根目录
        String projectRoot = ProjectPathUtils.getProjectRoot();
        // 构建文件路径
        String subPath = "form".equals(dslType) ? "form" : "list";
        return projectRoot + "/src/main/resources/" + dslBasePath + "/" + subPath + "/" + fileName + ".json";
    }

    /**
     * 加载提示词模板
     */
    private String loadPromptTemplate(String dslType) {
        try {
            String templateFile = "form".equals(dslType)
                ? "topezadmin/config/layui/dsl/prompts/ez_ai_form_prompt.md"
                : "topezadmin/config/layui/dsl/prompts/ez_ai_list_prompt.md";

            return   ConfigFileLoader.loadConfigFileContent(templateFile);
        } catch (Exception e) {
            log.error("加载提示词模板失败", e);
            return null;
        }
    }

    /**
     * 加载组件文档索引
     */
    private String loadComponentDocsIndex(DslModificationRequest request) {
        try {

            String content = ConfigFileLoader.loadConfigFileContent("topezadmin/config/layui/dsl/prompts/component.md");
            try {
            if (request!=null&&request.getComponentCode() != null && !request.getComponentCode().isEmpty()) {
                content = content + "\n\n## 选中组件 \n\n" + request.getComponentCode();

                String selectedComponentContent = ConfigFileLoader.loadConfigFileContent("topezadmin/config/layui/dsl/prompts/components/" + request.getComponentCode() + ".md");
                if (selectedComponentContent != null) {
                    content = content + "\n\n## 选中组件文档 \n\n" + selectedComponentContent;
                }
            }
            }catch (Exception e2){
                log.warn("加载选中组件文档失败", e2);
            }
            return "## 组件文档参考 \n\n" + content;
        } catch (Exception e) {
            log.warn("加载组件文档索引失败", e);
            return "";
        }
    }

    /**
     * 构建用户提示词
     */
    private String buildUserPrompt(Map<String, Object> dslContent, DslModificationRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("## 当前 DSL 配置\n\n");
        prompt.append("```json\n");
        prompt.append(JSONUtils.toJSONString(dslContent));
        prompt.append("\n```\n\n");

        prompt.append("## 用户需求\n\n");
        prompt.append(request.getUserRequirement()).append("\n\n");

        // 如果有选中的元素信息
        if (request.getItem_name() != null && !request.getItem_name().isEmpty()) {
            prompt.append("## 用户选中的元素\n\n");
            prompt.append("- **item_name**: ").append(request.getItem_name()).append("\n");

            if (request.getSelectedComponent() != null && !request.getSelectedComponent().isEmpty()) {
                prompt.append("- **组件类型**: ").append(request.getSelectedComponent()).append("\n");
            }

            prompt.append("\n请重点关注这个元素进行修改。\n\n");
        }

        prompt.append("## 要求\n\n");
        prompt.append(
                "1. 只返回一个JSON对象\n" +
                "2. 格式如下：\n" +
                "\n" +
                "{\n" +
                "  \"patch\": [ RFC6902数组 ],\n" +
                "  \"summary\": \"修改内容的摘要或者如果无法又该，则给出的理由\"\n" +
                "}\n" +
                "\n" +
                "3. 不要添加解释\n" +
                "4. patch 必须符合 RFC6902 标准\n" +
                "5. summary 不超过100字");

        return prompt.toString();
    }

    /**
     * 加载创建DSL的提示词模板
     */
    private String loadCreateDslPrompt(String dslType) {
        try {
            String templateFile = "form".equals(dslType)
                    ? "topezadmin/config/layui/dsl/prompts/ez_ai_form_prompt.md"
                    : "topezadmin/config/layui/dsl/prompts/ez_ai_list_prompt.md";

            // 如果创建提示词不存在，使用通用提示词
            String content = ConfigFileLoader.loadConfigFileContent(templateFile);
            if (content == null || content.isEmpty()) {
                log.warn("创建提示词模板不存在，使用默认模板");
                return getDefaultCreatePrompt(dslType);
            }
            return content;
        } catch (Exception e) {
            log.error("加载创建提示词模板失败", e);
            return getDefaultCreatePrompt(dslType);
        }
    }

    /**
     * 获取默认的创建提示词
     */
    private String getDefaultCreatePrompt(String dslType) {
        if ("form".equals(dslType)) {
            return "# 创建表单DSL配置\n\n" +
                    "你是一个专业的表单配置生成助手。根据用户提供的需求或SQL语句，生成完整的表单DSL配置。\n\n" +
                    "## 要求\n\n" +
                    "1. 返回格式：\n" +
                    "```json\n" +
                    "{\n" +
                    "  \"dsl\": { /* 完整的表单DSL配置 */ },\n" +
                    "  \"summary\": \"简短描述生成的表单\",\n" +
                    "  \"id\": \"建议的文件名（小写字母+连字符）\"\n" +
                    "}\n" +
                    "```\n\n" +
                    "2. DSL必须包含：\n" +
                    "   - title: 表单标题\n" +
                    "   - cardList: 表单卡片列表，每个卡片包含fieldList字段列表\n" +
                    "   - fieldList是二维数组：[[row1_fields], [row2_fields]]\n" +
                    "     第一层数组代表行，第二层代表该行中的字段\n" +
                    "   - 每个字段包含：item_name, label, component, classAppend等\n" +
                    "   - 使用classAppend定义宽度，如：classAppend=\"layui-col-md6\"（半行）\n\n" +
                    "3. 如果用户提供SQL，需要解析表结构生成字段\n" +
                    "4. 合理选择组件类型：input, select, date, textarea等\n" +
                    "5. 布局建议：每行通常放置2-3个相关字段\n";
        } else {
            return "# 创建列表DSL配置\n\n" +
                    "你是一个专业的列表配置生成助手。根据用户提供的需求或SQL语句，生成完整的列表DSL配置。\n\n" +
                    "## 要求\n\n" +
                    "1. 返回格式：\n" +
                    "```json\n" +
                    "{\n" +
                    "  \"dsl\": { /* 完整的列表DSL配置 */ },\n" +
                    "  \"summary\": \"简短描述生成的列表\",\n" +
                    "  \"id\": \"建议的文件名（小写字母+连字符）\"\n" +
                    "}\n" +
                    "```\n\n" +
                    "2. DSL必须包含：\n" +
                    "   - title: 列表标题\n" +
                    "   - column: 列配置列表，每列包含name, label, component等\n" +
                    "   - search: 搜索条件配置，使用二维数组：[[row1_searches], [row2_searches]]\n" +
                    "     第一层数组代表行，第二层代表该行中的搜索字段\n" +
                    "   - 每行默认放置4个搜索字段（不包含隐藏字段）\n" +
                    "   - 系统会自动为搜索字段设置 classAppend=\"layui-col-sm6 layui-col-md4 layui-col-lg4 layui-col-xl3\"\n" +
                    "   - express: 包含 main（查询SQL）、orderBy、count 等\n\n" +
                    "3. 如果用户提供SQL，需要解析字段生成列配置\n" +
                    "4. 合理配置搜索条件和列显示\n" +
                    "5. 布局建议：搜索字段每行通常放置4个字段\n";
        }
    }

    /**
     * 构建创建DSL的用户提示词
     */
    private String buildCreateDslUserPrompt(String dslType, String userRequirement) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("## 用户需求\n\n");
        prompt.append(userRequirement).append("\n\n");

        prompt.append("## 任务\n\n");
        prompt.append("请根据上述需求，生成完整的 ").append("form".equals(dslType) ? "表单" : "列表");
        prompt.append(" DSL配置。\n\n");
        prompt.append("##要求\n\n");
        prompt.append("1. 返回格式：\n");
        prompt.append("```json\n");
        prompt.append("{\n");
        prompt.append("  \"tables\": { /*  可能关联的数据库表，逗号分隔 */ },\n");
        prompt.append("  \"dsl\": { /* 完整的DSL配置 */ },\n");
        prompt.append("  \"summary\": \"简短描述生成的DSL\",\n");
        prompt.append("  \"id\": \"建议的文件名（小写字母+连字符）\"\n");
        prompt.append("}\n");
        prompt.append("```\n\n");
        prompt.append("## 注意事项\n\n");
        prompt.append("1. 如果需求中包含SQL语句，请解析SQL中的字段并生成对应的配置 如果没有SQL语句，返回的json的tables字段告知，可能关联的数据库表，逗号分隔 ，其他字段为空\n");
        prompt.append("2. 字段名（item_name）应该与数据库字段对应,且应该大写\n");
        prompt.append("3. 为每个字段选择合适的组件类型\n");
        prompt.append("4. 生成的id应该是有意义的英文名称（小写+连字符）\n");
        prompt.append("5. summary应该简洁明了地描述生成的内容\n");

        return prompt.toString();
    }

    /**
     * 读取 DSL 文件（支持文件和数据库两种数据源）
     */
    private Map<String, Object> loadDslFile(String fileName, String dslType) {
        try {
            log.info("开始加载 DSL: fileName={}, type={}", fileName, dslType);

            // 使用统一加载器（文件优先，数据库降级）
            top.ezadmin.dao.dto.DslConfig dslConfig = DslLoader.loadDsl(fileName, dslType);

            if (dslConfig == null) {
                log.warn("DSL 不存在: fileName={}, type={}", fileName, dslType);
                return null;
            }

            log.info("DSL 加载成功: fileName={}, type={}, source={}", fileName, dslType, dslConfig.getSource());
            return dslConfig.getConfig();

        } catch (Exception e) {
            log.error("加载 DSL 失败: fileName={}, type={}", fileName, dslType, e);
            return null;
        }
    }

    /**
     * 判断用户需求是否涉及表达式（SQL）修改
     * @param userRequirement 用户需求描述
     * @return true表示涉及表达式修改
     */
    private boolean isExpressModification(String userRequirement) {
        if (userRequirement == null || userRequirement.isEmpty()) {
            return false;
        }

        String requirement = userRequirement.toLowerCase();

        // 检查是否包含SQL相关关键词
        String[] sqlKeywords = {
            "sql", "查询", "表", "字段", "列",
            "where", "join", "select", "from",
            "express", "表达式", "数据库",
            "关联", "联表", "条件", "筛选"
        };

        for (String keyword : sqlKeywords) {
            if (requirement.contains(keyword)) {
                log.info("检测到表达式修改关键词: {}", keyword);
                return true;
            }
        }

        return false;
    }

    /**
     * 从DSL中提取SQL表达式
     * @param dslContent DSL内容
     * @return SQL表达式字符串
     */
    private String extractExpressFromDsl(Map<String, Object> dslContent) {
        StringBuilder expressBuilder = new StringBuilder();

        try {
            // 提取express字段
            Object expressObj = dslContent.get("express");
            if (expressObj != null) {
                if (expressObj instanceof Map) {
                    Map<String, Object> expressMap = (Map<String, Object>) expressObj;
                    for (Map.Entry<String, Object> entry : expressMap.entrySet()) {
                        expressBuilder.append(entry.getKey()).append(": ")
                                    .append(entry.getValue()).append("\n");
                    }
                } else {
                    expressBuilder.append(expressObj.toString()).append("\n");
                }
            }

            // 提取sqlId字段（列表页面可能使用）
            Object sqlIdObj = dslContent.get("sqlId");
            if (sqlIdObj != null) {
                expressBuilder.append("sqlId: ").append(sqlIdObj).append("\n");
            }

            // 提取datasource字段
            Object datasourceObj = dslContent.get("datasource");
            if (datasourceObj != null) {
                expressBuilder.append("datasource: ").append(datasourceObj).append("\n");
            }

        } catch (Exception e) {
            log.warn("提取DSL表达式失败", e);
        }

        String result = expressBuilder.toString();
        return result.isEmpty() ? "无" : result;
    }
}
