package org.dsahu.langchain.learning.employee.controller;

import lombok.extern.slf4j.Slf4j;
import org.dsahu.langchain.learning.employee.entity.EmployeeDocument;
import org.dsahu.langchain.learning.employee.service.EmployeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/{employeeId}")
    public EmployeeDocument getEmployee(@PathVariable String employeeId) {
        log.info("getEmployee# invoked text {}", employeeId);
        return employeeService.getEmployee(employeeId);
    }
}
