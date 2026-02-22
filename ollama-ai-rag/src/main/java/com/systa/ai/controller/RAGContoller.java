package com.systa.ai.controller;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api/rag")
public class RAGContoller {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RAGContoller (final ChatClient chatClient, final VectorStore vectorStore){
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    @Value("classpath:/promptTemplates/systemPromptRandomDataTemplate.st")
    Resource promptTemplate;

    @GetMapping("/chat")
    public ResponseEntity<String> randomChat(@RequestHeader("username") final String username,
                                             @RequestParam("message") final String message) {
        final SearchRequest searchRequest = SearchRequest
                .builder()
                .query(message)
                .topK(3)
                .similarityThreshold(0.5)
                .build();

        final List<Document> similarDocuments = vectorStore.similaritySearch(searchRequest);

        final String context = similarDocuments
                .stream()
                .map(Document :: getText)
                .collect(Collectors.joining(System.lineSeparator()));

        final String answer = chatClient
                .prompt()
                .system(promptSystemSpec -> promptSystemSpec.text(promptTemplate).param("documents", context))
                .advisors(a -> a.param(CONVERSATION_ID, username))
                .user(message)
                .call()
                .content();

        return ResponseEntity.ok(answer);


    }
}
