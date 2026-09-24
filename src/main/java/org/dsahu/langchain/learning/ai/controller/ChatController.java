package org.dsahu.langchain.learning.ai.controller;

import lombok.extern.slf4j.Slf4j;
import org.dsahu.langchain.learning.ai.assistant.Assistant;
import org.dsahu.langchain.learning.employee.dto.Employee;
import org.dsahu.langchain.learning.ai.assistant.EmployeeAssistant;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class ChatController {
    private final Assistant assistant;
    private final EmployeeAssistant employeeAssistant;

    public ChatController(Assistant assistant, EmployeeAssistant employeeAssistant) {
        this.assistant = assistant;
        this.employeeAssistant = employeeAssistant;
    }


    @GetMapping("/hello")
    public String hello(@RequestParam String topic,@RequestParam String level) {
       return assistant.getChatResponseFromOpenAI(topic, level);
    }

    @GetMapping("/lifesupport")
    public String lifeSupport(@RequestParam String name,@RequestParam String question) {
        return assistant.lifeSupport(name, question);
    }

    @PostMapping("/employee")
    public Employee extractEmployee(@RequestBody String text) {
        log.info("extractEmployee# invoked text {}", text);
        return employeeAssistant.extractEmployee(text);
    }


    @GetMapping("/conversation")
    public Employee conversation(@RequestParam String userName,@RequestParam String message) {
        log.info("conversation# invoked for userName {} and message {}",userName, message);
        return employeeAssistant.conversation(userName, message);
    }

}
