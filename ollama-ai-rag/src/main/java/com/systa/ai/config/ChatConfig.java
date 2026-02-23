package com.systa.ai.config;

import com.systa.ai.advisors.TokenUsageAuditAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatConfig {

    @Bean("defaultChatClient")
    public ChatClient getChatClientWithMemory(final ChatClient.Builder chatClientBuilder,
                                              final ChatMemory chatMemory,
                                              final RetrievalAugmentationAdvisor retrievalAugmentationAdvisor){

        final Advisor loggerAdvisor = new SimpleLoggerAdvisor();
        final Advisor tokenUsageAuditAdvisor = new TokenUsageAuditAdvisor();
        final Advisor chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(chatMemory).build();

        return chatClientBuilder
                .defaultAdvisors(List.of(loggerAdvisor, tokenUsageAuditAdvisor, chatMemoryAdvisor, retrievalAugmentationAdvisor))
                .build();
    }

    @Bean
    public RetrievalAugmentationAdvisor retrievalAugmentationAdvisor(final VectorStore vectorStore){

        final DocumentRetriever documentRetriever = VectorStoreDocumentRetriever
                .builder()
                .vectorStore(vectorStore)
                .topK(3)
                .similarityThreshold(0.5)
                .build();

        return RetrievalAugmentationAdvisor
                .builder().
                documentRetriever(documentRetriever)
                .build();
    }
}
