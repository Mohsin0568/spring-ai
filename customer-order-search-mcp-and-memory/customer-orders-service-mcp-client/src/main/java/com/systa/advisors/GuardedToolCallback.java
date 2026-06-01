package com.systa.advisors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.systa.exception.GuardrailViolationException;
import com.systa.session.UserContext;
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
    public String call(final String toolInput) {
        final String toolName = delegate.getToolDefinition().name();
        validateToolName(toolName);
        validateArguments(toolName, toolInput);

        // inject userId into the tool input json

        final String enrichedInput = injectUserId(toolInput);

        logger.info("Tool call approved — tool: [{}], input: [{}]", toolName, enrichedInput);
        return delegate.call(enrichedInput);
    }

    private String injectUserId(final String toolInput) {
        try {
            final String userId = UserContext.get();
            if (userId == null || userId.isBlank()) {
                throw new GuardrailViolationException("Missing userId in session context");
            }
            final ObjectNode node = (ObjectNode) OBJECT_MAPPER.readTree(toolInput);
            final ObjectNode requestNode = (ObjectNode) node.get("request");
            requestNode.put("customerId", userId);
            return OBJECT_MAPPER.writeValueAsString(node);
        } catch (GuardrailViolationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new GuardrailViolationException("Failed to inject userId into tool input");
        }
    }

    private void validateToolName(final String toolName) {
        if (!ALLOWED_TOOLS.contains(toolName)) {
            logger.error("Guardrail blocked unauthorized tool call: [{}]", toolName);
            throw new GuardrailViolationException(
                "Unauthorized tool call: only search operations are permitted.");
        }
    }

    private void validateArguments(final String toolName, final String toolInput) {
        if (toolInput == null || toolInput.isBlank()) {
            return;
        }
        try {
            final JsonNode root = OBJECT_MAPPER.readTree(toolInput);
            for (var entry : root.properties()) {
                final String fieldName = entry.getKey();
                final JsonNode fieldValue = entry.getValue();

                if (!ALLOWED_ARGUMENT_FIELDS.contains(fieldName)) {
                    logger.error("Guardrail blocked unrecognized argument field [{}] for tool [{}]", fieldName, toolName);
                    throw new GuardrailViolationException(
                        "Unrecognized argument field in tool call: " + fieldName);
                }

                if (fieldValue.isTextual()) {
                    validateStringValue(fieldName, fieldValue.asText());
                }
            }
        } catch (final GuardrailViolationException ex) {
            throw ex;
        } catch (final Exception ex) {
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