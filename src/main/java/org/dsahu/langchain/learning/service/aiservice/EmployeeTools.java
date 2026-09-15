package org.dsahu.langchain.learning.service.aiservice;

import dev.langchain4j.agent.tool.Tool;
import org.dsahu.langchain.learning.entity.business.EmployeeDocument;
import org.dsahu.langchain.learning.service.EmployeeService;

import org.springframework.stereotype.Component;

@Component
public class EmployeeTools {
    private final EmployeeService employeeService;

    public EmployeeTools(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Tool("Get employee information using employee ID")
    public EmployeeDocument getEmployee(String employeeId) {
        return employeeService.getEmployee(employeeId);
    }
}
