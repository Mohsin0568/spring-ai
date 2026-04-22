package com.systa.controller;

import com.systa.domain.PurchaseOrderDomain;
import com.systa.tools.PurchaseOrderTool;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class QueryController {

    private final ChatClient chatClient;

    private final Resource systemMessageForQueryGeneration;

    private final Resource systemMessageForPOGeneration;

    private final PurchaseOrderTool purchaseOrderTool;

    public QueryController(final ChatClient chatClient,
                           final PurchaseOrderTool purchaseOrderTool,
                           @Value("classpath:/promptTemplates/purchase_order_query_system_message.st") final Resource systemMessageForQueryGeneration,
                           @Value("classpath:/promptTemplates/purchase_order_query_system_message_for_tools.st") final Resource systemMessageForPOGeneration){
        this.chatClient = chatClient;
        this.systemMessageForQueryGeneration = systemMessageForQueryGeneration;
        this.systemMessageForPOGeneration = systemMessageForPOGeneration;
        this.purchaseOrderTool = purchaseOrderTool;
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

    @GetMapping("/generate-purchase-order")
    @ResponseBody
    public List<PurchaseOrderDomain> generatePurchaseOrders(@RequestParam("query") final String query){
        return chatClient
                .prompt()
                .system(systemMessageForPOGeneration)
                .user(query)
                .tools(purchaseOrderTool)
                .call()
                .entity(new ParameterizedTypeReference<List<PurchaseOrderDomain>>() {});
    }
}
