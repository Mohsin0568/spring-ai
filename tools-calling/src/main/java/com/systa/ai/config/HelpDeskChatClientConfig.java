package com.systa.ai.config;

import com.systa.ai.advisors.TokenUsageAuditAdvisor;
import com.systa.ai.tools.TimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;

@Configuration
public class HelpDeskChatClientConfig {

    @Value("classpath:/promptTemplates/helpDeskSystemPromptTemplate.st")
    Resource systemPromptTemplate;

    @Bean("helpDeskChatClient")
    public ChatClient getChatClientWithMemory(final ChatClient.Builder chatClientBuilder,
                                              final ChatMemory chatMemory,
                                              final TimeTools timeTools){

        final Advisor loggerAdvisor = new SimpleLoggerAdvisor();
        final Advisor tokenUsageAuditAdvisor = new TokenUsageAuditAdvisor();
        final Advisor chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        return chatClientBuilder
                .defaultSystem(systemPromptTemplate)
                .defaultTools(timeTools)
                .defaultAdvisors(List.of(loggerAdvisor, tokenUsageAuditAdvisor, chatMemoryAdvisor))
                .build();
    }
}
