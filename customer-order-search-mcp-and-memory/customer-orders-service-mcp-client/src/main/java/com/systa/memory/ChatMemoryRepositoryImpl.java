package com.systa.memory;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Persists Spring AI chat conversations in MongoDB. Each conversation is stored as a single
 * document so that the full message window is replaced atomically on every {@link #saveAll}
 * call, mirroring how {@code MessageWindowChatMemory} replays "existing + new, then trim".
 */
@Component
@RequiredArgsConstructor
public class ChatMemoryRepositoryImpl implements ChatMemoryRepository {

    private final UserChatMemoryRepository repository;

    @Override
    public List<String> findConversationIds() {
        return repository.findAll().stream()
                .map(UserChatMemoryDocument::conversationId)
                .toList();
    }

    @Override
    public List<Message> findByConversationId(final String conversationId) {
        return repository.findById(conversationId)
                .map(UserChatMemoryDocument::messages)
                .orElseGet(List::of)
                .stream()
                .map(ChatMemoryRepositoryImpl::toMessage)
                .toList();
    }

    @Override
    public void saveAll(final String conversationId, final List<Message> messages) {
        final List<StoredMessage> storedMessages = messages.stream()
                .map(ChatMemoryRepositoryImpl::toStoredMessage)
                .toList();
        repository.save(new UserChatMemoryDocument(conversationId, storedMessages, Instant.now()));
    }

    @Override
    public void deleteByConversationId(final String conversationId) {
        repository.deleteById(conversationId);
    }

    private static StoredMessage toStoredMessage(final Message message) {
        return switch (message) {
            case AssistantMessage assistantMessage -> new StoredMessage(
                    MessageType.ASSISTANT.name(),
                    assistantMessage.getText(),
                    assistantMessage.getToolCalls().stream()
                            .map(toolCall -> new StoredToolCall(toolCall.id(), toolCall.type(), toolCall.name(), toolCall.arguments()))
                            .toList(),
                    List.of());
            case ToolResponseMessage toolResponseMessage -> new StoredMessage(
                    MessageType.TOOL.name(),
                    null,
                    List.of(),
                    toolResponseMessage.getResponses().stream()
                            .map(response -> new StoredToolResponse(response.id(), response.name(), response.responseData()))
                            .toList());
            case SystemMessage systemMessage -> new StoredMessage(
                    MessageType.SYSTEM.name(), systemMessage.getText(), List.of(), List.of());
            default -> new StoredMessage(
                    MessageType.USER.name(), message.getText(), List.of(), List.of());
        };
    }

    private static Message toMessage(final StoredMessage stored) {
        return switch (MessageType.valueOf(stored.type())) {
            case ASSISTANT -> AssistantMessage.builder()
                    .content(stored.content())
                    .toolCalls(stored.toolCalls().stream()
                            .map(toolCall -> new AssistantMessage.ToolCall(toolCall.id(), toolCall.type(), toolCall.name(), toolCall.arguments()))
                            .toList())
                    .build();
            case TOOL -> ToolResponseMessage.builder()
                    .responses(stored.toolResponses().stream()
                            .map(response -> new ToolResponseMessage.ToolResponse(response.id(), response.name(), response.responseData()))
                            .toList())
                    .build();
            case SYSTEM -> new SystemMessage(stored.content());
            case USER -> new UserMessage(stored.content());
        };
    }
}
