package org.dsahu.langchain.learning.ai.assistant;


import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;


@AiService
public interface Assistant {
    @SystemMessage("""
    You are a Java teacher.
    Explain concepts simply.
    Always provide examples.
    """)
    @UserMessage("""
             Explain {{topic}} to a beginner.
                Give {{number}} examples.
            """)
    String getChatResponseFromOpenAI(@V("topic") String topic,
                                     @V("number") String level);

    @SystemMessage(fromResource = "/prompt/system-customer-support.txt")
    @UserMessage(fromResource = "/prompt/customer-support.txt")
    String lifeSupport(
            @V("customerName") String customerName,
            @V("question") String question
    );


}
