package com.systa.config;

import com.systa.advisors.GuardedToolCallback;
import com.systa.advisors.InputGuardrailAdvisor;
import com.systa.advisors.TokenAuditAdvisor;
import com.systa.advisors.ToolCallGuardrailAdvisor;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@AllArgsConstructor
public class ChatConfig {

    private final TokenAuditAdvisor tokenAuditAdvisor;
    private final InputGuardrailAdvisor inputGuardrailAdvisor;
    private final ToolCallGuardrailAdvisor toolCallGuardrailAdvisor;

    @Bean
    public ChatClient getChatClient(final ChatClient.Builder chatClientBuilder,
                                    final ToolCallbackProvider toolCallbackProvider){

        ToolCallback[] guardedCallbacks = Arrays.stream(toolCallbackProvider.getToolCallbacks())
                .map(GuardedToolCallback::new)
                .toArray(ToolCallback[]::new);

        return chatClientBuilder
                .defaultAdvisors(List.of(inputGuardrailAdvisor, tokenAuditAdvisor, toolCallGuardrailAdvisor, new SimpleLoggerAdvisor()))
                .defaultToolCallbacks(guardedCallbacks)
                .build();
    }
}
