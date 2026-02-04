package com.systa.apenai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(final ChatClient chatClient){
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam("message") final String message){
        return chatClient
                .prompt(message)
//                .system("""
//                        You are an internal HR assistant. Your role is to help\s
//                        employees with questions related to HR policies, such as\s
//                        leave policies, working hours, benefits, and code of conduct.
//                        If a user asks for help with anything outside of these topics,\s
//                        kindly inform them that you can only assist with queries related to\s
//                        HR policies.
//                        """)
                .call()
                .content();
    }
}
