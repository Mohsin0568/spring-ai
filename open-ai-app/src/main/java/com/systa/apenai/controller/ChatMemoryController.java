package com.systa.apenai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api")
public class ChatMemoryController {

    private final ChatClient chatClient;

    public ChatMemoryController(final @Qualifier("chatClientWithMemory") ChatClient chatClient){
        this.chatClient = chatClient;
    }

    @GetMapping("/chat-memory")
    public String chatWithMemory(@RequestParam("message") final String message,
                                 @RequestHeader("userName") final String userName){
        return chatClient
                .prompt(message)
                .advisors(advisorSpec -> advisorSpec.param(CONVERSATION_ID, userName))
                .call()
                .content();
    }
}
