package com.systa.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

@RestController
@RequestMapping("/api/tools-calling")
public class ToolsCallingController {

    private final ChatClient chatClient;

    public ToolsCallingController (@Qualifier("toolsCallingChatClient") final ChatClient chatClient){
        this.chatClient = chatClient;
    }

    @GetMapping("/local-time")
    public ResponseEntity<String> localTime(@RequestHeader("username") final String username,
                                            @RequestParam("message") final String message){

        final String answer =  chatClient
                .prompt()
                .advisors(a -> a.param(CONVERSATION_ID, username))
                .user(message)
                .call()
                .content();

        return ResponseEntity.ok(answer);
    }
}
