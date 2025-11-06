package com.example.config;

import com.example.functions.MyToolCallResultConverter;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.execution.ToolCallResultConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AiConfig {

    @Value("${spring.ai.provider:ollama}")
    private String aiProvider;

    @Value("${spring.ai.ollama.base-url}")
    private String ollamaBaseUrl;

    @Value("${spring.ai.ollama.chat.options.model}")
    private String ollamaModel;

    @Value("${spring.ai.openai.base-url}")
    private String openAiBaseUrl;

    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    @Value("${spring.ai.openai.chat.options.model}")
    private String openAiModel;

    @Bean
    @Primary
    public ChatModel chatModel() {
        switch (aiProvider.toLowerCase()) {
            case "ollama":
                return ollamaChatModel();
            case "openai":
                return openAiChatModel();
            default:
                throw new IllegalArgumentException("Unsupported AI provider: " + aiProvider);
        }
    }

    @Bean
    public OllamaChatModel ollamaChatModel() {
        // 使用正确的构造函数
        OllamaApi ollamaApi = OllamaApi.builder().baseUrl(ollamaBaseUrl).build();
        return OllamaChatModel.builder().ollamaApi(ollamaApi).defaultOptions(
                OllamaOptions.builder().model(ollamaModel).build()
        ).build();
    }
    @Bean
    public OpenAiChatModel openAiChatModel() {
        // 使用正确的构造函数
        OpenAiApi openAiApi = OpenAiApi.builder().baseUrl(openAiBaseUrl).apiKey(openAiApiKey)
                 .build();
        return OpenAiChatModel.builder().openAiApi(openAiApi).defaultOptions(
                OpenAiChatOptions.builder().model(openAiModel).build()
        ).build();
    }
    
    @Bean
    public ToolCallResultConverter toolCallResultConverter() {
        return new MyToolCallResultConverter();
    }
}