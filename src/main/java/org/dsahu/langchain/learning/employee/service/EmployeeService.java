package org.dsahu.langchain.learning.employee.service;

import lombok.extern.slf4j.Slf4j;
import org.dsahu.langchain.learning.employee.entity.EmployeeDocument;
import org.dsahu.langchain.learning.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public EmployeeDocument getEmployee(String employeeId) {
        log.info("getEmployee# invoked text {}", employeeId);
        return employeeRepository.findById(employeeId)
                .orElse(null);
    }
}