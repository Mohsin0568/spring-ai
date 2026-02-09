package com.systa.apenai.config;

import com.systa.apenai.TokenAuditAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatConfig {

    private final TokenAuditAdvisor tokenAuditAdvisor;

    ChatConfig(final TokenAuditAdvisor tokenAuditAdvisor){
        this.tokenAuditAdvisor = tokenAuditAdvisor;
    }

    @Bean
    public ChatClient getChatClient(final ChatClient.Builder chatClientBuilder){
        return chatClientBuilder
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), tokenAuditAdvisor))
                .defaultSystem("""
                        You are an internal HR assistant. Your role is to help\s
                        employees with questions related to HR policies, such as\s
                        leave policies, working hours, benefits, and code of conduct.
                        If a user asks for help with anything outside of these topics,\s
                        kindly inform them that you can only assist with queries related to\s
                        HR policies.
                        """)
                .defaultAdvisors()
                .build();
    }
}
