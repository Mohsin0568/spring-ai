package com.systa.memory;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "chat_memory")
public record UserChatMemoryDocument(@Id String conversationId,
                                     List<StoredMessage> messages,
                                     Instant updatedAt) {
}
