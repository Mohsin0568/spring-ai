package com.systa.memory;

import java.util.List;

public record StoredMessage(String type,
                             String content,
                             List<StoredToolCall> toolCalls,
                             List<StoredToolResponse> toolResponses) {
}
