package org.dsahu.langchain.learning.employee.repository;

import org.dsahu.langchain.learning.employee.entity.EmployeeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepository
        extends MongoRepository<EmployeeDocument, String> {
}