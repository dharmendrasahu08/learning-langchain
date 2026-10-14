package org.dsahu.langchain.learning.ai.tools;

import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.dsahu.langchain.learning.employee.entity.EmployeeDocument;
import org.dsahu.langchain.learning.employee.service.EmployeeService;

import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmployeeTools {
    private final EmployeeService employeeService;

    public EmployeeTools(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Tool("Get employee information using employee ID")
    public EmployeeDocument getEmployee(String employeeId) {
        log.info("getEmployee# tool is invoked for empID {}", employeeId);
        return employeeService.getEmployee(employeeId);
    }
}
