package org.dsahu.langchain.learning.service;

import org.dsahu.langchain.learning.entity.business.EmployeeDocument;
import org.dsahu.langchain.learning.repo.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public EmployeeDocument getEmployee(String employeeId) {

        return employeeRepository
                .findById(employeeId)
                .orElse(null);
    }
}