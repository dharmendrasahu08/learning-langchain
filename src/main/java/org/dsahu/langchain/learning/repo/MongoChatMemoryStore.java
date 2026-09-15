package org.dsahu.langchain.learning.repo;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageJsonCodec;
import dev.langchain4j.data.message.JacksonChatMessageJsonCodec;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.dsahu.langchain.learning.entity.ai.ChatMemoryDocument;

import java.util.List;

@Component
public class MongoChatMemoryStore implements ChatMemoryStore {

    private final MongoTemplate mongoTemplate;
    private final ChatMessageJsonCodec codec;

    public MongoChatMemoryStore(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.codec = new JacksonChatMessageJsonCodec();
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {

        ChatMemoryDocument document =
                mongoTemplate.findById(
                        memoryId.toString(),
                        ChatMemoryDocument.class
                );

        if (document == null) {
            return List.of();
        }

        return codec.messagesFromJson(
                document.getMessagesJson()
        );
    }

    @Override
    public void updateMessages(
            Object memoryId,
            List<ChatMessage> messages) {

        String messagesJson =
                codec.messagesToJson(messages);

        ChatMemoryDocument document =
                new ChatMemoryDocument(
                        memoryId.toString(),
                        messagesJson
                );

        mongoTemplate.save(document);
    }

    @Override
    public void deleteMessages(Object memoryId) {

        ChatMemoryDocument document =
                mongoTemplate.findById(
                        memoryId.toString(),
                        ChatMemoryDocument.class
                );

        if (document != null) {
            mongoTemplate.remove(document);
        }
    }
}

