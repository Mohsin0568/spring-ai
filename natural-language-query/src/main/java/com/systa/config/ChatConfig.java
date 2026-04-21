package com.systa.config;

import com.systa.advisors.TokenAuditAdvisor;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@AllArgsConstructor
public class ChatConfig {

    private final TokenAuditAdvisor tokenAuditAdvisor;

    @Bean
    public ChatClient getChatClient(final ChatClient.Builder chatClientBuilder){

        return chatClientBuilder
                .defaultAdvisors(List.of(new SimpleLoggerAdvisor(), tokenAuditAdvisor))
                .build();
    }
}
