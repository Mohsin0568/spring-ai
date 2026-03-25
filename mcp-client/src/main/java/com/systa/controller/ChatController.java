package com.systa.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(final ChatClient.Builder chatClientBuilder, final ToolCallbackProvider toolCallbackProvider) {
        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(toolCallbackProvider)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

      @GetMapping("/chat")
      public ResponseEntity<String> sendMessage(@RequestParam("message") final String message) {
          final String response = chatClient
                  .prompt()
                  .user(message)
                  .call()
                  .content();

          return ResponseEntity.ok(response);
      }
}
