package org.dsahu.langchain.learning.resume.repository;

import java.util.List;

import org.dsahu.langchain.learning.resume.dto.ResumeSearchRequest;
import org.dsahu.langchain.learning.resume.entity.ResumeDocument;

public interface ResumeSearchRepository {
	List<ResumeDocument> search(ResumeSearchRequest request);
}
