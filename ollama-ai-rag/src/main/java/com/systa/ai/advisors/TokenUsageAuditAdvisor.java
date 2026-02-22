package com.systa.ai.advisors;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;

public class TokenUsageAuditAdvisor implements CallAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(TokenUsageAuditAdvisor.class);

    @Override
    public ChatClientResponse adviseCall(final ChatClientRequest chatClientRequest,
                                         final CallAdvisorChain callAdvisorChain) {
        final ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        final ChatResponse chatResponse = chatClientResponse.chatResponse();
        if(chatResponse.getMetadata() != null) {
            final Usage usage = chatResponse.getMetadata().getUsage();

            if(usage != null) {
                logger.info("Token usage details : {}",usage.toString());
            }
        }
        return chatClientResponse;
    }

    @Override
    public String getName() {
        return "TokenUsageAuditAdvisor";
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
