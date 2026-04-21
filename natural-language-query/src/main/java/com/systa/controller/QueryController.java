package com.systa.controller;

import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class QueryController {

    private final ChatClient chatClient;

    private Resource systemMessage;

    public QueryController(final ChatClient chatClient,
                           @Value("classpath:/promptTemplates/purchase_order_query_system_message.st") final Resource systemMessage){
        this.chatClient = chatClient;
        this.systemMessage = systemMessage;
    }

    @GetMapping("/chat")
    public String getMessage(@RequestParam("message") final String message){
        return chatClient.prompt(message).call().content();
    }

    @GetMapping("/generate-query")
    public String generateQuery(@RequestParam("query") final String query){
        return chatClient
                .prompt()
                .system(systemMessage)
                .user(query)
                .call()
                .content();
    }
}
