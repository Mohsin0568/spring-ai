package com.systa.controller;

import com.systa.domain.CustomerOrderDomain;
import com.systa.session.UserContext;
import com.systa.validation.ValidSearchQuery;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class QueryController {

    private final ChatClient chatClient;
    private final Resource systemMessageForQueryGeneration;
    private final Resource systemMessageForOrderGeneration;

    public QueryController(final ChatClient chatClient,
                           @Value("classpath:/promptTemplates/customer_order_query_system_message.st") final Resource systemMessageForQueryGeneration,
                           @Value("classpath:/promptTemplates/customer_order_query_system_message_for_tools.st") final Resource systemMessageForOrderGeneration){
        this.chatClient = chatClient;
        this.systemMessageForQueryGeneration = systemMessageForQueryGeneration;
        this.systemMessageForOrderGeneration = systemMessageForOrderGeneration;
    }

    @GetMapping("/chat")
    public String getMessage(@ValidSearchQuery @RequestParam("message") final String message,
                             @RequestHeader(value = "X-User-Id", required = false) final String userId){
        return chatClient.prompt(message)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationIdFor(userId)))
                .call()
                .content();
    }

    @GetMapping("/generate-query")
    public String generateQuery(@ValidSearchQuery @RequestParam("query") final String query,
                                @RequestHeader(value = "X-User-Id", required = false) final String userId){
        return chatClient
                .prompt()
                .system(systemMessageForQueryGeneration)
                .user(query)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationIdFor(userId)))
                .call()
                .content();
    }

    @GetMapping("/orders")
    public List<CustomerOrderDomain> generateCustomerOrders(@ValidSearchQuery @RequestParam("query") final String query,
                                                            @RequestHeader("X-User-Id") final String userId){
        UserContext.set(userId);
        try {
            return chatClient
                    .prompt()
                    .system(systemMessageForOrderGeneration)
                    .user(query)
                    .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationIdFor(userId)))
                    .call()
                    .entity(new ParameterizedTypeReference<>() {
                    });
        }finally {
            UserContext.clear();
        }
    }

    @GetMapping("/v1/orders")
    public String generateCustomerOrdersV1(@ValidSearchQuery @RequestParam("query") final String query,
                                                            @RequestHeader("X-User-Id") final String userId){
        UserContext.set(userId);
        try {
            return chatClient
                    .prompt()
                    .system(systemMessageForOrderGeneration)
                    .user(query)
                    .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationIdFor(userId)))
                    .call()
                    .content();
        }finally {
            UserContext.clear();
        }
    }

    /**
     * Each user gets their own running conversation so follow-up queries (e.g. "what is the
     * total quantity") are resolved against the previous turn's context (e.g. "pending delivery
     * orders"). Requests without an X-User-Id fall back to a single shared conversation.
     */
    private static String conversationIdFor(final String userId){
        return (userId == null || userId.isBlank()) ? ChatMemory.DEFAULT_CONVERSATION_ID : userId;
    }
}
