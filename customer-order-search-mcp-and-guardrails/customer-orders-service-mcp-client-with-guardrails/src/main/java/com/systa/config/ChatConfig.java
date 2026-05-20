package com.systa.config;

import com.systa.advisors.InputGuardrailAdvisor;
import com.systa.advisors.TokenAuditAdvisor;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@AllArgsConstructor
public class ChatConfig {

    private final TokenAuditAdvisor tokenAuditAdvisor;
    private final InputGuardrailAdvisor inputGuardrailAdvisor;

    @Bean
    public ChatClient getChatClient(final ChatClient.Builder chatClientBuilder,
                                    final ToolCallbackProvider toolCallbackProvider){

        return chatClientBuilder
                .defaultAdvisors(List.of(inputGuardrailAdvisor, tokenAuditAdvisor, new SimpleLoggerAdvisor()))
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
    }
}
