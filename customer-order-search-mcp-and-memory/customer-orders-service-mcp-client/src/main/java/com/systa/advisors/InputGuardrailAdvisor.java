package com.systa.advisors;

import com.systa.service.QueryValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

@Component
public class InputGuardrailAdvisor implements CallAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(InputGuardrailAdvisor.class);

    private final QueryValidationService queryValidationService;

    public InputGuardrailAdvisor(QueryValidationService queryValidationService) {
        this.queryValidationService = queryValidationService;
    }

    @Override
    public ChatClientResponse adviseCall(final ChatClientRequest chatClientRequest, final CallAdvisorChain callAdvisorChain) {
        final String userText = chatClientRequest.prompt()
                .getInstructions()
                .stream()
                .filter(msg -> msg instanceof UserMessage)
                .map(msg -> msg.getText())
                .findFirst()
                .orElse("");

        logger.debug("InputGuardrailAdvisor validating user message before LLM call");
        queryValidationService.validate(userText);

        return callAdvisorChain.nextCall(chatClientRequest);
    }

    @Override
    public String getName() {
        return "Input Guardrail Advisor";
    }

    // Order 0 ensures this runs before TokenAuditAdvisor (order=1),
    // so invalid requests are rejected before any tokens are consumed.
    @Override
    public int getOrder() {
        return 0;
    }
}
