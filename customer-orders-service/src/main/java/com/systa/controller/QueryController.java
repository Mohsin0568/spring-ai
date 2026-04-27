package com.systa.controller;

import com.systa.domain.CustomerOrderDomain;
import com.systa.tools.CustomerOrderTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class QueryController {

    private final ChatClient chatClient;
    private final Resource systemMessageForQueryGeneration;
    private final Resource systemMessageForOrderGeneration;
    private final CustomerOrderTool customerOrderTool;

    public QueryController(final ChatClient chatClient,
                           final CustomerOrderTool customerOrderTool,
                           @Value("classpath:/promptTemplates/customer_order_query_system_message.st") final Resource systemMessageForQueryGeneration,
                           @Value("classpath:/promptTemplates/customer_order_query_system_message_for_tools.st") final Resource systemMessageForOrderGeneration){
        this.chatClient = chatClient;
        this.systemMessageForQueryGeneration = systemMessageForQueryGeneration;
        this.systemMessageForOrderGeneration = systemMessageForOrderGeneration;
        this.customerOrderTool = customerOrderTool;
    }

    @GetMapping("/chat")
    public String getMessage(@RequestParam("message") final String message){
        return chatClient.prompt(message).call().content();
    }

    @GetMapping("/generate-query")
    public String generateQuery(@RequestParam("query") final String query){
        return chatClient
                .prompt()
                .system(systemMessageForQueryGeneration)
                .user(query)
                .call()
                .content();
    }

    @GetMapping("/orders")
    public List<CustomerOrderDomain> generateCustomerOrders(@RequestParam("query") final String query){
        return chatClient
                .prompt()
                .system(systemMessageForOrderGeneration)
                .user(query)
                .tools(customerOrderTool)
                .call()
                .entity(new ParameterizedTypeReference<List<CustomerOrderDomain>>() {});
    }
}
