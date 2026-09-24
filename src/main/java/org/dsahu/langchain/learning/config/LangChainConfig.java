package org.dsahu.langchain.learning.config;


import org.springframework.context.annotation.Configuration;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.context.annotation.Bean;

@Configuration
public class LangChainConfig {
    //Configuration for in-momory chat
    /*@Bean(name="chatMemory")
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.withMaxMessages(10);
    }*/

    @Bean
    public ChatMemoryProvider chatMemoryProvider(
            ChatMemoryStore chatMemoryStore) {

        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(chatMemoryStore)
                .build();
    }
}
