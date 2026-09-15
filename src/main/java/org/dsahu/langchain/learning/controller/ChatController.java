package org.dsahu.langchain.learning.controller;

import org.dsahu.langchain.learning.service.aiservice.Assistant;
import org.dsahu.langchain.learning.model.Employee;
import org.dsahu.langchain.learning.service.aiservice.EmployeeAssistant;
import org.springframework.web.bind.annotation.*;

@RestController
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
        return employeeAssistant.extractEmployee(text);
    }


    @GetMapping("/conversation")
    public Employee conversation(@RequestParam String userName,@RequestParam String message) {
        return employeeAssistant.conversation(userName, message);
    }

}
