package com.systa.ai.config;

import com.systa.ai.advisors.TokenUsageAuditAdvisor;
import com.systa.ai.rag.PIIMaskingDocumentPostProcessor;
import com.systa.ai.rag.WebSearchDocumentRetriever;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.List;

@Configuration
public class WebSearchClientConfig {

    @Bean("webSearchRAGChatClient")
    public ChatClient chatClient(final ChatClient.Builder chatClientBuilder,
                                 final ChatMemory chatMemory, final RestClient.Builder restClientBuilder) {
        final Advisor loggerAdvisor = new SimpleLoggerAdvisor();
        final Advisor tokenUsageAdvisor = new TokenUsageAuditAdvisor();
        final Advisor memoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();
        final var webSearchRAGAdvisor = RetrievalAugmentationAdvisor.builder()
                .queryTransformers(TranslationQueryTransformer
                        .builder()
                        .chatClientBuilder(chatClientBuilder.clone())
                        .targetLanguage("english")
                        .build()
                )
                .documentRetriever(WebSearchDocumentRetriever.builder()
                        .restClientBuilder(restClientBuilder).maxResults(5).build())
                .documentPostProcessors(PIIMaskingDocumentPostProcessor.builder())
                .build();
        return chatClientBuilder
                .defaultAdvisors(List.of(loggerAdvisor, memoryAdvisor, tokenUsageAdvisor,
                        webSearchRAGAdvisor))
                .build();
    }
}
