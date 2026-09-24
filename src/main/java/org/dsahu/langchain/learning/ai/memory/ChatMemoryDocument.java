package org.dsahu.langchain.learning.ai.memory;

import org.dsahu.langchain.learning.common.constant.CollectionName;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = CollectionName.CHAT_MEMORY)
public class ChatMemoryDocument {

    @Id
    private String memoryId;

    private String messagesJson;

    public ChatMemoryDocument() {
    }

    public ChatMemoryDocument(String memoryId, String messagesJson) {
        this.memoryId = memoryId;
        this.messagesJson = messagesJson;
    }

    public String getMemoryId() {
        return memoryId;
    }

    public void setMemoryId(String memoryId) {
        this.memoryId = memoryId;
    }

    public String getMessagesJson() {
        return messagesJson;
    }

    public void setMessagesJson(String messagesJson) {
        this.messagesJson = messagesJson;
    }
}
