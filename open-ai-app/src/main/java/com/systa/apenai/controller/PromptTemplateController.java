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
public class PromptTemplateController {

    @Value("classpath:/promptTemplates/userPromptTemplate.st")
    private Resource promptUserMessage;

    private final ChatClient chatClient;

    public PromptTemplateController(final ChatClient chatClient){
        this.chatClient = chatClient;
    }

    @GetMapping("/email")
    public String generateEmail(@RequestParam("customerName") final String customerName,
                                @RequestParam("customerMessage") final String customerMessage){

        return chatClient
                .prompt()
                .system("""
                        You are a professional customer service assistant which helps drafting email
                        responses to improve the productivity of the customer support team
                        """)
                .user(promptUserSpec -> promptUserSpec.text(promptUserMessage)
                        .param("customerName", customerName)
                        .param("customerMessage", customerMessage))
                .call()
                .content();

    }
}
