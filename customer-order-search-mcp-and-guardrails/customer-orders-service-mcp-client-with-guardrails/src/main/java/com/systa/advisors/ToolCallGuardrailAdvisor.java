package com.systa.advisors;

import com.systa.exception.GuardrailViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.stereotype.Component;

@Component
public class ToolCallGuardrailAdvisor implements CallAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(ToolCallGuardrailAdvisor.class);

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        logger.debug("ToolCallGuardrailAdvisor: entering guarded tool execution phase");

        try {
            ChatClientResponse response = callAdvisorChain.nextCall(chatClientRequest);
            logger.debug("ToolCallGuardrailAdvisor: tool execution completed successfully");
            return response;
        } catch (GuardrailViolationException ex) {
            // GuardedToolCallback already logged the detail — just re-throw.
            throw ex;
        } catch (Exception ex) {
            // Spring AI may wrap tool exceptions; unwrap to check if a guardrail caused it.
            Throwable cause = ex.getCause();
            while (cause != null) {
                if (cause instanceof GuardrailViolationException gex) {
                    logger.error("ToolCallGuardrailAdvisor: guardrail violation unwrapped from tool exception — {}", gex.getMessage());
                    throw gex;
                }
                cause = cause.getCause();
            }
            throw ex;
        }
    }

    @Override
    public String getName() {
        return "Tool Call Guardrail Advisor";
    }

    // Order 2 makes this the innermost advisor, running closest to the LLM call.
    // Execution order: InputGuardrailAdvisor(0) → TokenAuditAdvisor(1) → ToolCallGuardrailAdvisor(2) → LLM
    @Override
    public int getOrder() {
        return 2;
    }
}