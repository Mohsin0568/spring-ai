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
public class OutputGuardrailAdvisor implements CallAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(OutputGuardrailAdvisor.class);

    @Override
    public ChatClientResponse adviseCall(final ChatClientRequest chatClientRequest, final CallAdvisorChain callAdvisorChain) {
        logger.debug("ToolCallGuardrailAdvisor: entering guarded tool execution phase");

        try {
            final ChatClientResponse response = callAdvisorChain.nextCall(chatClientRequest);
            checkForOperationNotPermitted(response);
            logger.debug("ToolCallGuardrailAdvisor: tool execution completed successfully");
            return response;
        } catch (final GuardrailViolationException ex) {
            // GuardedToolCallback already logged the detail — just re-throw.
            throw ex;
        } catch (final Exception ex) {
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

    // Inspects the LLM text response for the OPERATION_NOT_PERMITTED sentinel defined
    // in the system prompt, which the LLM emits when it detects a policy violation itself.
    private void checkForOperationNotPermitted(final ChatClientResponse response) {
        final var chatResponse = response.chatResponse();
        if (chatResponse == null || chatResponse.getResult() == null) {
            return;
        }
        final String text = chatResponse.getResult().getOutput().getText();
        if (text != null && text.contains("OPERATION_NOT_PERMITTED")) {
            logger.warn("ToolCallGuardrailAdvisor: LLM returned OPERATION_NOT_PERMITTED sentinel — rejecting response");
            throw new GuardrailViolationException(
                "Operation not permitted: the requested action violates the system's security policy.");
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