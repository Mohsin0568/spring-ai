package com.systa.advisors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.systa.exception.GuardrailViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;

import java.util.Set;

/**
 * Wraps a ToolCallback to enforce an allowlist on tool names and validate
 * argument fields/values before the actual tool executes.
 */
public class GuardedToolCallback implements ToolCallback {

    private static final Logger logger = LoggerFactory.getLogger(GuardedToolCallback.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Set<String> ALLOWED_TOOLS = Set.of("search_customer_orders");

    // Derived from CustomerOrderSearchRequest record fields.
    private static final Set<String> ALLOWED_ARGUMENT_FIELDS = Set.of(
        "orderId", "orderStatus",
        "customerId", "customerName",
        "email", "phone",
        "postCode", "city", "country",
        "productName", "productId", "minQuantity", "maxQuantity",
        "deliveryDateFrom", "deliveryDateTo", "orderPlacementFrom", "orderPlacementTo",
        "limit", "sortBy", "sortDirection", "request"
    );

    // MongoDB operator injection markers and other dangerous patterns.
    private static final Set<String> BLOCKED_VALUE_PATTERNS = Set.of(
        "$where", "$eval", "$function", "mapReduce", "eval("
    );

    private final ToolCallback delegate;

    public GuardedToolCallback(ToolCallback delegate) {
        this.delegate = delegate;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return delegate.getToolDefinition();
    }

    @Override
    public String call(String toolInput) {
        String toolName = delegate.getToolDefinition().name();
        validateToolName(toolName);
        validateArguments(toolName, toolInput);
        logger.info("Tool call approved — tool: [{}], input: [{}]", toolName, toolInput);
        return delegate.call(toolInput);
    }

    private void validateToolName(String toolName) {
        if (!ALLOWED_TOOLS.contains(toolName)) {
            logger.error("Guardrail blocked unauthorized tool call: [{}]", toolName);
            throw new GuardrailViolationException(
                "Unauthorized tool call: only search operations are permitted.");
        }
    }

    private void validateArguments(String toolName, String toolInput) {
        if (toolInput == null || toolInput.isBlank()) {
            return;
        }
        try {
            JsonNode root = OBJECT_MAPPER.readTree(toolInput);
            for (var entry : root.properties()) {
                String fieldName = entry.getKey();
                JsonNode fieldValue = entry.getValue();

                if (!ALLOWED_ARGUMENT_FIELDS.contains(fieldName)) {
                    logger.error("Guardrail blocked unrecognized argument field [{}] for tool [{}]", fieldName, toolName);
                    throw new GuardrailViolationException(
                        "Unrecognized argument field in tool call: " + fieldName);
                }

                if (fieldValue.isTextual()) {
                    validateStringValue(fieldName, fieldValue.asText());
                }
            }
        } catch (GuardrailViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Guardrail failed to parse tool arguments for tool [{}]: {}", toolName, ex.getMessage());
            throw new GuardrailViolationException("Invalid tool argument format.");
        }
    }

    private void validateStringValue(String fieldName, String value) {
        if (value.startsWith("$")) {
            logger.error("Guardrail blocked potential NoSQL injection in field [{}]: [{}]", fieldName, value);
            throw new GuardrailViolationException(
                "Potential injection detected in tool argument: " + fieldName);
        }
        for (String blocked : BLOCKED_VALUE_PATTERNS) {
            if (value.contains(blocked)) {
                logger.error("Guardrail blocked dangerous pattern [{}] in field [{}]", blocked, fieldName);
                throw new GuardrailViolationException(
                    "Dangerous pattern detected in tool argument: " + fieldName);
            }
        }
    }
}