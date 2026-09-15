package org.dsahu.langchain.learning.repo;

import org.dsahu.langchain.learning.entity.business.EmployeeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmployeeRepository
        extends MongoRepository<EmployeeDocument, String> {
}