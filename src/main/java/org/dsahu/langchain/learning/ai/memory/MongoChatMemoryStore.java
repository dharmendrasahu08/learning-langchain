package org.dsahu.langchain.learning.ai.memory;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageJsonCodec;
import dev.langchain4j.data.message.JacksonChatMessageJsonCodec;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Component
@Slf4j
public class MongoChatMemoryStore implements ChatMemoryStore {

    private final MongoTemplate mongoTemplate;
    private final ChatMessageJsonCodec codec;

    public MongoChatMemoryStore(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.codec = new JacksonChatMessageJsonCodec();
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        log.info("getMessages#Loading chat memory for memoryId={}", memoryId);
        ChatMemoryDocument document =
                mongoTemplate.findById(
                        memoryId.toString(),
                        ChatMemoryDocument.class
                );

        if (document == null) {
            log.info("getMessages#No chat memory found for memoryId={}", memoryId);
            return List.of();
        }


        List<ChatMessage> messages= codec.messagesFromJson(
                document.getMessagesJson()
        );
        log.info("getMessages# Loaded {} messages for memoryId={}",messages.size(),memoryId);
        return messages;
    }

    @Override
    public void updateMessages(
            Object memoryId,
            List<ChatMessage> messages) {
        log.info("Saving {} messages for memoryId={}", messages.size(),memoryId);
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

