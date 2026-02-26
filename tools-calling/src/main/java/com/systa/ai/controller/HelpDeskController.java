package com.systa.ai.controller;

import com.systa.ai.tools.HelpDeskTicketTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/help-desk")
public class HelpDeskController {

    private final ChatClient helpDeskChatClient;
    private final HelpDeskTicketTool helpDeskTicketTool;

    public HelpDeskController(@Qualifier("helpDeskChatClient") final  ChatClient helpDeskChatClient,
                              final HelpDeskTicketTool helpDeskTicketTool) {
        this.helpDeskChatClient = helpDeskChatClient;
        this.helpDeskTicketTool = helpDeskTicketTool;
    }

    @GetMapping
    public ResponseEntity<String> createTicket(@RequestHeader("username") final String username,
                                               @RequestParam("message") final String message) {

        final String answer = helpDeskChatClient
                .prompt()
                .advisors(advisorSpec -> advisorSpec.param("conversationId", username))
                .tools(helpDeskTicketTool)
                .user(message)
                .toolContext(Map.of("userName", username))
                .call()
                .content();

        return ResponseEntity.ok(answer);
    }
}
