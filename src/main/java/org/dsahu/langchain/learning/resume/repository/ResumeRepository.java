package org.dsahu.langchain.learning.resume.repository;

import org.dsahu.langchain.learning.resume.entity.ResumeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResumeRepository extends MongoRepository<ResumeDocument, String> {
}