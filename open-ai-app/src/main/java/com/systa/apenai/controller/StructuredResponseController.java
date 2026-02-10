package com.systa.apenai.controller;

import com.systa.apenai.model.CountryCitiesModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StructuredResponseController {

    private final ChatClient chatClient;

    public StructuredResponseController(final ChatClient.Builder chatClientBuilder){
        this.chatClient = chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    @GetMapping("/cities")
    public ResponseEntity<CountryCitiesModel> chat(@RequestParam("message") final String message){
        final CountryCitiesModel countryCitiesModel = chatClient
                .prompt(message)
                .call()
                .entity(CountryCitiesModel.class);

        return ResponseEntity.ok(countryCitiesModel);
    }
}
