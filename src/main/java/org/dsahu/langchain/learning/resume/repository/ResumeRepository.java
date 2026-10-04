package org.dsahu.langchain.learning.resume.repository;

import java.util.List;
import java.util.Optional;

import org.dsahu.langchain.learning.resume.entity.ResumeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResumeRepository extends MongoRepository<ResumeDocument, String> {
	Optional<ResumeDocument> findByProfileId(String profileId);
	List<ResumeDocument> findByProfileIdIn(List<String> profileIds);
	
}