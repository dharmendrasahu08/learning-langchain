package org.dsahu.langchain.learning.controller;

import org.dsahu.langchain.learning.entity.business.EmployeeDocument;
import org.dsahu.langchain.learning.service.EmployeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/{employeeId}")
    public EmployeeDocument getEmployee(
            @PathVariable String employeeId) {

        return employeeService.getEmployee(employeeId);
    }
}
