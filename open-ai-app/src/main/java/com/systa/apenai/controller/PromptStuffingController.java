package com.systa.apenai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PromptStuffingController {

    @Value("classpath:/promptTemplates/systemPromptTemplate.st")
    private Resource systemMessage;

    private final ChatClient chatClient;

    public PromptStuffingController(final ChatClient chatClient){
        this.chatClient = chatClient;
    }

    @GetMapping("/promptStuffing")
    public String promptStuffing(@RequestParam("message") final String message){
        return chatClient
                .prompt()
                .system(systemMessage)
                .user(message)
                .call()
                .content();
    }
}
