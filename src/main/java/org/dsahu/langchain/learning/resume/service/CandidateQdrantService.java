package org.dsahu.langchain.learning.resume.service;

import java.util.List;

import org.dsahu.langchain.learning.resume.dto.CandidateSearchResponse;
import org.dsahu.langchain.learning.resume.entity.ResumeDocument;

public interface CandidateQdrantService {
	void createCollection();
	void upsertCandidate(String profileId, ResumeDocument.CandidateProfile profile);
	List<CandidateSearchResponse> searchCandidates(String query, int limit);
	void syncCandidatesToQdrant();
	void batchUpsertCandidates(List<ResumeDocument> documents);
	
}
