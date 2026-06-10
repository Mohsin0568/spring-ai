package com.systa.memory;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserChatMemoryRepository extends MongoRepository<UserChatMemoryDocument, String> {
}
