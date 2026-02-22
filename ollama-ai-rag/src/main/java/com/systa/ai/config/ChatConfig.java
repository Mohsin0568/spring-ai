package com.systa.ai.config;

import com.systa.ai.advisors.TokenUsageAuditAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatConfig {

    @Bean()
    public ChatClient getChatClientWithMemory(final ChatClient.Builder chatClientBuilder,
                                              final ChatMemory chatMemory){

        final Advisor loggerAdvisor = new SimpleLoggerAdvisor();
        final Advisor tokenUsageAuditAdvisor = new TokenUsageAuditAdvisor();
        final Advisor chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        return chatClientBuilder
                .defaultAdvisors(List.of(loggerAdvisor, tokenUsageAuditAdvisor, chatMemoryAdvisor))
                .build();
    }
}
