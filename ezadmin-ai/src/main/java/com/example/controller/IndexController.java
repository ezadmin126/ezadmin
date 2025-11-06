package com.example.controller;

import com.example.functions.EzadminFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import top.ezadmin.EzBootstrap;
import top.ezadmin.common.utils.EzProxy;
import top.ezadmin.common.utils.JSONUtils;
import top.ezadmin.common.utils.StringUtils;
import top.ezadmin.common.utils.Utils;
import top.ezadmin.plugins.express.InsertSimpleOperator;
import top.ezadmin.plugins.express.OperatorParam;
import top.ezadmin.plugins.express.UpdateSimpleOperator;
import top.ezadmin.plugins.express.jdbc.InsertParam;
import top.ezadmin.plugins.express.jdbc.UpdateParam;
import top.ezadmin.service.ListService;
import top.ezadmin.web.EzResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
@SessionAttributes("chatHistory")
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
    
    // 定义上下文长度限制
    private static final int MAX_CONTEXT_LENGTH = 32000; // 根据模型限制调整此值
    private static final int MAX_HISTORY_SIZE = 21; // 最大历史记录数

    @Autowired
    private ChatModel chatModel;
    
    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    EzadminFunctions ezadminFunctions;
    ListService listService = EzProxy.singleInstance(ListService.class);

    @RequestMapping
    public String index() {
        return "chat";
    }

    @GetMapping("/chatlist")
    public String chatPage(String code, Model model) {
        model.addAttribute("code",code);
        return "chatlist";
    }
    
    @PostMapping("/publish")
    @ResponseBody
    public EzResult publish(@RequestParam("listUrlCode") String listUrlCode) throws Exception {
        logger.info("=== 开始发布列表 ===");
        logger.info("列表编码: {}", listUrlCode);

        String ENCRYPT_LIST_ID = listUrlCode;
        Map<String, Object> c = JSONUtils.parseObjectMap(listService.selectDbPublishListById(ENCRYPT_LIST_ID));
        //生成如果有数据 //1.把生产的复制到历史表
        if (Utils.isNotEmpty(c)) {
            String config = c.get("EZ_CONFIG") + "";
            String DATASOURCE = c.get("DATASOURCE") + "";
            String name = c.get("EZ_NAME") + "";
            String code = ENCRYPT_LIST_ID;
            Map<String, Object> requestParamMap = new HashMap<>();
            requestParamMap.put("EZ_CONFIG", config);
            requestParamMap.put("DATASOURCE", DATASOURCE);
            requestParamMap.put("EZ_NAME", name);
            requestParamMap.put("EZ_CODE", code);
            requestParamMap.put("EZ_TYPE", 1);
            OperatorParam op = new OperatorParam();
            op.setParams(requestParamMap);
            op.setDs(EzBootstrap.getInstance().getEzDataSource());
            Utils.addParam(op);
            InsertSimpleOperator o = new InsertSimpleOperator();
            InsertParam param = new InsertParam();
            param.table("T_EZADMIN_HISTORY");
            param.add("#{EZ_CODE}");
            param.add("#{DATASOURCE}");
            param.add("#{EZ_NAME}");
            param.add("#{EZ_TYPE,value=1}");
            param.add("#{EZ_CONFIG}");
            param.add("#{ADD_TIME,value=NOW()}");
            param.add("#{UPDATE_TIME,value=NOW()}");
            param.add("#{IS_DEL,value=0}");
            o.executeInner(new Object[]{param});
        }

        //2.把edit复制到生产
        Map<String, Object> edit = listService.selectConfigEditList(ENCRYPT_LIST_ID);
        if (Utils.isEmpty(edit)) {
            logger.warn("未找到列表配置: {}", listUrlCode);
            return EzResult.instance().fail("未找到列表配置");
        }
        
        String config = edit.get("EZ_CONFIG") + "";
        String DATASOURCE = edit.get("DATASOURCE") + "";
        String name = edit.get("EZ_NAME") + "";
        String code = ENCRYPT_LIST_ID;
        if (Utils.isEmpty(c)) {
            Map<String, Object> requestParamMap = new HashMap<>();
            requestParamMap.put("EZ_CONFIG", config);
            requestParamMap.put("DATASOURCE", DATASOURCE);
            requestParamMap.put("EZ_NAME", name);
            requestParamMap.put("EZ_CODE", code);
            requestParamMap.put("EZ_TYPE", 1);
            OperatorParam op = new OperatorParam();
            op.setParams(requestParamMap);
            op.setDs(EzBootstrap.getInstance().getEzDataSource());
            Utils.addParam(op);
            InsertSimpleOperator o = new InsertSimpleOperator();
            InsertParam param = new InsertParam();
            param.table("T_EZADMIN_PUBLISH");
            param.add("#{EZ_CODE}");
            param.add("#{DATASOURCE}");
            param.add("#{EZ_NAME}");
            param.add("#{EZ_TYPE,value=1}");
            param.add("#{EZ_CONFIG}");
            param.add("#{ADD_TIME,value=NOW()}");
            param.add("#{IS_DEL,value=0}");
            o.executeInner(new Object[]{param});
        } else {
            Map<String, Object> requestParamMap = new HashMap<>();
            requestParamMap.put("EZ_CONFIG", config);
            requestParamMap.put("DATASOURCE", DATASOURCE);
            requestParamMap.put("EZ_NAME", name);
            requestParamMap.put("EZ_CODE", code);
            requestParamMap.put("EZ_TYPE", 1);
            OperatorParam op = new OperatorParam();
            op.setParams(requestParamMap);
            op.setDs(EzBootstrap.getInstance().getEzDataSource());
            Utils.addParam(op);
            UpdateSimpleOperator o = new UpdateSimpleOperator();
            UpdateParam param = new UpdateParam();
            param.table("T_EZADMIN_PUBLISH");
            param.add("#{EZ_CODE}");
            param.add("#{DATASOURCE}");
            param.add("#{EZ_NAME}");
            param.add("#{EZ_CONFIG}");
            param.add("#{UPDATE_TIME,value=NOW()}");
            param.where(" where EZ_CODE=#{EZ_CODE} and EZ_TYPE=1");
            o.executeInner(new Object[]{param});
        }
        //3.刷新缓存
        EzBootstrap.clear();
        logger.info("=== 列表发布成功 ===");
        return EzResult.instance();
    }

    /**
     * 接收用户输入,调用 Ollama 模型
     * @param request 包含用户输入的请求对象
     * @return 模型返回的响应数据
     */
    @PostMapping("/chat")
    @ResponseBody
    public Map<String, Object> chat(@RequestBody Map<String, String> request, jakarta.servlet.http.HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String userInput = request.get("message");
            String code = request.get("code");
            if(StringUtils.isBlank(code)){
                code=System.currentTimeMillis()+"";
            }
            userInput="当前【列表编码】为："+code+","+userInput;
            if (userInput == null || userInput.trim().isEmpty()) {
                response.put("success", false);
                response.put("error", "用户输入不能为空");
                return response;
            }

            // 获取或创建聊天历史记录
            List<Message> chatHistory = (List<Message>) session.getAttribute("chatHistory");
            if (chatHistory == null) {
                chatHistory = new ArrayList<>();
                // 添加系统提示词
                String systemPrompt = loadSystemPrompt();
                if (systemPrompt != null && !systemPrompt.isEmpty()) {
                    chatHistory.add(new SystemMessage(systemPrompt));
                }
                session.setAttribute("chatHistory", chatHistory);
            }

            // 将用户消息添加到历史记录中
            chatHistory.add(new UserMessage(userInput));

            // 检查并处理上下文长度
            handleContextLength(chatHistory);

            // 打印所有提示词到日志
            logChatHistory(chatHistory);

            // 使用 ChatClient 调用 Ollama 模型，传递历史记录
            ChatClient chatClient = ChatClient.create(chatModel);
            logger.info("=== 开始请求AI ===");

            // 调用AI并获取响应
            String finalResponse = chatClient
                    .prompt()
                    .messages(chatHistory)
                    .tools(ezadminFunctions)
                    .user(userInput)
                    .call()
                    .content();
                    
            logger.info("=== 请求AI  成功==={}", finalResponse);
            if(StringUtils.isBlank(finalResponse)){
                return null;
            }
            // 记录工具调用日志
            logToolCalls(finalResponse);

            // 将AI响应添加到历史记录中
            chatHistory.add(new AssistantMessage(finalResponse));

            // 限制历史记录长度，防止过长，但保留系统提示词
            if (chatHistory.size() > MAX_HISTORY_SIZE) { // 最多保留10轮对话 + 1个系统提示词
                // 保留系统提示词（第一个元素）和最近的消息
                Message firstMessage = chatHistory.get(0);
                if (firstMessage instanceof SystemMessage) {
                    chatHistory.subList(1, chatHistory.size() - (MAX_HISTORY_SIZE - 1)).clear();
                } else {
                    chatHistory.subList(0, chatHistory.size() - (MAX_HISTORY_SIZE - 1)).clear();
                }
            }

            response.put("success", true);
            response.put("isThink", isThinkingProcess(finalResponse));
            response.put("userInput", userInput);
            response.put("aiResponse", finalResponse);
            response.put("model", "qwen3:4b");
            
        } catch (Exception e) {
            logger.error("=== 请求AI  失败===", e);
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        System.out.println(response);
        return response;
    }
    
    /**
     * 检查并处理上下文长度
     * @param chatHistory 聊天历史记录
     */
    private void handleContextLength(List<Message> chatHistory) {
        // 计算当前上下文总长度
        int totalLength = chatHistory.stream()
                .mapToInt(message -> message.getText().length())
                .sum();
        
        logger.info("当前上下文长度: {} 字符", totalLength);
        
        // 如果超过限制，记录日志并清理旧消息
        if (totalLength > MAX_CONTEXT_LENGTH) {
            logger.warn("上下文长度超过限制 ({} > {})，正在自动清理旧消息", totalLength, MAX_CONTEXT_LENGTH);
            
            // 使用先进先出原则移除旧的用户消息和助手消息
            // 保留系统消息和最近的消息
            Message firstMessage = chatHistory.get(0);
            boolean hasSystemMessage = firstMessage instanceof SystemMessage;
            
            // 移除旧消息直到长度合适或只剩下系统消息和少量最近消息
            while (totalLength > MAX_CONTEXT_LENGTH && chatHistory.size() > (hasSystemMessage ? 3 : 2)) {
                // 移除最旧的用户消息和助手消息对
                if (hasSystemMessage && chatHistory.size() > 3) {
                    // 移除第二个元素（第一个用户消息）
                    chatHistory.remove(1);
                    // 如果还有助手消息，也移除它
                    if (chatHistory.size() > 2 && chatHistory.get(1) instanceof AssistantMessage) {
                        chatHistory.remove(1);
                    }
                } else if (!hasSystemMessage && chatHistory.size() > 2) {
                    // 移除第一个元素（用户消息）
                    chatHistory.remove(0);
                    // 如果还有助手消息，也移除它
                    if (!chatHistory.isEmpty() && chatHistory.get(0) instanceof AssistantMessage) {
                        chatHistory.remove(0);
                    }
                }
                
                // 重新计算长度
                totalLength = chatHistory.stream()
                        .mapToInt(message -> message.getText().length())
                        .sum();
            }
            
            logger.info("清理后上下文长度: {} 字符", totalLength);
        }
    }
    
    /**
     * 判断是否为思考过程
     * @param aiResponse AI响应内容
     * @return 是否为思考过程
     */
    private boolean isThinkingProcess(String aiResponse) {
        // 检查是否包含工具调用的特征
        return aiResponse != null && (
            aiResponse.contains("```json") || 
            aiResponse.contains("\"tool\"") || 
            aiResponse.contains("\"function\"") ||
            aiResponse.startsWith("{") // JSON格式的工具调用
        );
    }
    
    /**
     * 记录工具调用日志
     * @param aiResponse AI响应内容
     */
    private void logToolCalls(String aiResponse) {
        try {
            // 检查响应中是否包含工具调用
            if (aiResponse != null && aiResponse.contains("```")) {
                logger.info("=== 检测到工具调用 ===");
                logger.info("工具调用内容: {}", aiResponse);
                logger.info("=== 工具调用结束 ===");
            }
        } catch (Exception e) {
            logger.error("记录工具调用日志时出错", e);
        }
    }
    
    /**
     * 加载系统提示词
     * @return 系统提示词内容
     */
    private String loadSystemPrompt() {
        try {
            Resource resource = resourceLoader.getResource("classpath:ezadmin.md");
            if (resource.exists()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                    return reader.lines().collect(Collectors.joining("\n"));
                }
            }
        } catch (IOException e) {
            // 如果加载失败，返回空字符串
            e.printStackTrace();
        }
        return "";
    }
    
    /**
     * 将聊天历史记录打印到日志
     * @param chatHistory 聊天历史记录
     */
    private void logChatHistory(List<Message> chatHistory) {
        logger.info("=== 开始打印聊天历史记录 ===");
        for (int i = 0; i < chatHistory.size(); i++) {
            Message message = chatHistory.get(i);
            String role = "";
            if (message instanceof SystemMessage) {
                role = "System";
            } else if (message instanceof UserMessage) {
                role = "User";
            } else if (message instanceof AssistantMessage) {
                role = "Assistant";
            }
            
            logger.info("Message {}: [{}] {}", i, role, message.getText());
        }
        logger.info("=== 结束打印聊天历史记录 ===");
    }
}