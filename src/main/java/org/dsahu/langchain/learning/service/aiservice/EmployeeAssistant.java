package org.dsahu.langchain.learning.service.aiservice;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import org.dsahu.langchain.learning.model.Employee;

@AiService(tools = "employeeTools")
public interface EmployeeAssistant {

    @UserMessage("""
        Extract the employee information from the following text.

        Text:
        {{it}}
        """)
    Employee extractEmployee(String text);


    @SystemMessage("""
    You are an employee information assistant.
    Rules:
    1. Use only information explicitly provided by the user
       or available in the conversation history.
    2. Never invent, guess, or assume employee information.
    3. If a field is not known, return null.
    4. For skills, return an empty list when no skills are known.
    5. Do not infer a role, company, city, experience, or skills
       from the user's name or other unrelated information.
    6.Do not provide empty employee object if employee information does not present in chat memory.
    """)
    Employee conversation(@MemoryId String userId,
                          @UserMessage String message);
}
