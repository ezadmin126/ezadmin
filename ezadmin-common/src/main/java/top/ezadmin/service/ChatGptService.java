package top.ezadmin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ezadmin.EzBootstrap;
import top.ezadmin.common.utils.JSONUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ChatGPT API 服务（兼容阿里云千问等 OpenAI 格式接口）
 * JDK8/JDK17兼容版本
 */
public class ChatGptService {
    private static final Logger log = LoggerFactory.getLogger(ChatGptService.class);
    private static final ChatGptService chatGptService = new ChatGptService();

    private ChatGptService() {
    }

    public static ChatGptService getInstance() {
        return chatGptService;
    }
    /**
     * 调用 AI 进行单轮对话
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @return AI 返回的内容
     */
    public String chat(String systemPrompt, String userPrompt) {
        List<Map<String, String>> messages = new ArrayList<>();

        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, String> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);
        }

        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userPrompt);
        messages.add(userMsg);

        return chatWithMessages(messages);
    }

    /**
     * 调用 AI 进行多轮对话
     *
     * @param messages 消息列表，每个消息包含 role 和 content
     * @return AI 返回的内容
     */
    public String chatWithMessages(List<Map<String, String>> messages) {
        try {
            // 构建请求参数
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", EzBootstrap.config().getModel());
            requestBody.put("messages", messages);
            requestBody.put("temperature", EzBootstrap.config().getTemperature());

            Map<String, Object> response_format = new HashMap<>();
            response_format.put("type", "json_object");
            requestBody.put("response_format", response_format);

            log.info("AI 请求: {}", JSONUtils.toJSONString(requestBody ));

            // 发送请求（使用 JDK 自带的 HttpURLConnection）
            String responseBody = sendPostRequest(
                    EzBootstrap.config().getApiUrl(),
                    JSONUtils.toJSONString(requestBody),
                    EzBootstrap.config().getApiKey()
            );
            log.info("AI 响应: {}", responseBody);

            // 解析响应
            Map<String,Object> jsonResponse = JSONUtils.parseObjectMap(responseBody);

            // 检查是否有错误
            if (jsonResponse.containsKey("error")) {
                Map<String, Object> error = (Map<String, Object>) jsonResponse.get("error");
                String errorMessage = (String) error.get("message");
                log.error("AI API 错误: {}", errorMessage);
                throw new RuntimeException("AI API 调用失败: " + errorMessage);
            }

            // 提取返回内容
            List<Map<String, Object>> choices = (List<Map<String, Object>>) jsonResponse.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("AI API 返回内容为空");
            }

            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            String content = (String) message.get("content");

            return content;

        } catch (Exception e) {
            log.error("调用 AI API 异常", e);
            throw new RuntimeException("调用 AI 服务失败: " + e.getMessage(), e);
        }
    }

    /**
     * 调用 AI 并期望返回 JSON 格式
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户提示词
     * @return 解析后的 JSONObject
     */
    public Map<String, Object> chatForJson(String systemPrompt, String userPrompt) {
        String response = chat(systemPrompt, userPrompt);

        // 尝试提取 JSON（可能被 markdown 代码块包裹）
        String jsonStr = extractJson(response);

        try {
            return JSONUtils.parseObjectMap(jsonStr);
        } catch (Exception e) {
            log.error("解析 AI 返回的 JSON 失败，原始内容: {}", response);
            throw new RuntimeException("AI 返回内容不是有效的 JSON 格式: " + e.getMessage());
        }
    }

    /**
     * 从响应中提取 JSON 内容（处理 markdown 代码块等情况）
     */
    private String extractJson(String response) {
        if (response == null || response.isEmpty()) {
            throw new RuntimeException("AI 返回内容为空");
        }

        // 去除首尾空白
        response = response.trim();

        // 如果被 ```json ``` 包裹，提取出来
        if (response.startsWith("```json")) {
            int start = response.indexOf("```json") + 7;
            int end = response.lastIndexOf("```");
            if (end > start) {
                response = response.substring(start, end).trim();
            }
        } else if (response.startsWith("```")) {
            int start = response.indexOf("```") + 3;
            int end = response.lastIndexOf("```");
            if (end > start) {
                response = response.substring(start, end).trim();
            }
        }

        return response;
    }

    /**
     * 使用 JDK 自带的 HttpURLConnection 发送 POST 请求
     * 兼容 JDK8/JDK17
     *
     * @param url     请求 URL
     * @param jsonBody JSON 请求体
     * @param apiKey  API 密钥
     * @return 响应内容
     */
    private String sendPostRequest(String url, String jsonBody, String apiKey) throws Exception {
        HttpURLConnection connection = null;
        try {
            // 创建连接
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();

            // 设置请求方法和属性
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // 设置超时时间（120秒）
            connection.setConnectTimeout(120000);
            connection.setReadTimeout(120000);

            // 允许输出
            connection.setDoOutput(true);

            // 写入请求体
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 读取响应
            int responseCode = connection.getResponseCode();
            BufferedReader reader;

            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString();

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
