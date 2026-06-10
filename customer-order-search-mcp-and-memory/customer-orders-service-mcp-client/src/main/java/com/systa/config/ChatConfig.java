package com.systa.config;

import com.systa.advisors.GuardedToolCallback;
import com.systa.advisors.InputGuardrailAdvisor;
import com.systa.advisors.TokenAuditAdvisor;
import com.systa.advisors.OutputGuardrailAdvisor;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@AllArgsConstructor
public class ChatConfig {

    private final TokenAuditAdvisor tokenAuditAdvisor;
    private final InputGuardrailAdvisor inputGuardrailAdvisor;
    private final OutputGuardrailAdvisor outputGuardrailAdvisor;

    @Bean
    public ChatMemory chatMemory(final ChatMemoryRepository chatMemoryRepository,
                                 @Value("${app.chat-memory.max-messages:20}") final int maxMessages){
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(maxMessages)
                .build();
    }

    @Bean
    public ChatClient getChatClient(final ChatClient.Builder chatClientBuilder,
                                    final ToolCallbackProvider toolCallbackProvider,
                                    final ChatMemory chatMemory){

        ToolCallback[] guardedCallbacks = Arrays.stream(toolCallbackProvider.getToolCallbacks())
                .map(GuardedToolCallback::new)
                .toArray(ToolCallback[]::new);

        return chatClientBuilder
                .defaultAdvisors(List.of(
                        inputGuardrailAdvisor,
                        tokenAuditAdvisor,
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        outputGuardrailAdvisor,
                        new SimpleLoggerAdvisor()))
                .defaultToolCallbacks(guardedCallbacks)
                .build();
    }
}
